package com.github.nbuesing.kafka.connect.opensky;

import com.github.nbuesing.kafka.connect.opensky.api.BoundingBox;
import com.github.nbuesing.kafka.connect.opensky.api.OpenSky;
import com.github.nbuesing.kafka.connect.opensky.api.Records;
import com.github.nbuesing.kafka.connect.opensky.converter.RecordConverter;
import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class OpenSkySourceTask extends SourceTask {

    private BlockingQueue<SourceRecord> queue = null;
    private String topic = null;

    private long lastTimestamp;
    private long maxTimestamp;

    private long interval;
    private String url;
    private String username = null;
    private String password = null;

    private boolean first = true;

    private OpenSky openSky;

    private List<BoundingBox> boundingBoxes;

    @Override
    public String version() {
        return new OpenSkySourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {

        OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props);

        queue = new LinkedBlockingQueue<>();
        topic = config.getTopic();
        url = config.getOpenskyUrl();

        if (StringUtils.isNotBlank(url) && !url.endsWith("/")) {
            url += "/";
        }

        config.getInterval().ifPresent(value -> interval = value * 1000L);

        config.getOpenskyUsername().ifPresent(value -> {
                    username = value;
                    password = config.getOpenskyPassword().get();
                }
        );

        openSky = new OpenSky(url, username, password);

        boundingBoxes = config.getBoundingBoxes();
    }

    private void getStates() {
        boundingBoxes.forEach(this::getStates);
    }

    private void getStates(final BoundingBox boundingBox) {
        try {

            // opensky will apply the world filter, which might cause it to be less performant, so if world box
            // is indeed provided, use null instead.
            //OpenSkyStates os = openSky.getStates(0, null, BoundingBoxUtil.isWorld(boundingBox) ? null : boundingBox);
            Records os = openSky.getAircrafts(boundingBox);

            if (os == null) {
                log.warn("unable to make request, if you have more than 1 task running you need to have an account that allows for it.");
                //return;
                throw new RuntimeException("TODO");
            }

            //int size = (os.getStates() != null) ? os.getStates().size() : 0;

            log.info("Processing timestamp={}, numRecords={}, boundingBox={}", os.getTime(), os.getStates().size(), BoundingBoxUtil.toString(boundingBox));

            maxTimestamp = 0L;

            //TEMP
            final long timestamp = System.currentTimeMillis();

            os.getStates().forEach(vector -> {

                final String icao24 = vector.getIcao24();

                // we are assuming that open-sky doesn't have "late arriving data" so we do not need to keep
                // offsets for each flight, just the "max offset".
                // if such assumption was proven to be wrong, would keep track / flight
                //   vector.getLastContact().longValue() * 1000L;
                if (timestamp > maxTimestamp) {
                    maxTimestamp = timestamp;
                }

                if (timestamp > lastTimestamp
                        && vector.getLatitude() != null
                        && vector.getLongitude() != null
                ) {

                    final Struct struct = RecordConverter.convert(vector);

                    try {
                        struct.validate();

                        log.debug("aircraft transponder={}, timestamp={}", icao24, timestamp);

                        SourceRecord record = new SourceRecord(null, null, topic, null, RecordConverter.SCHEMA_KEY, vector.getIcao24().trim(), RecordConverter.SCHEMA, struct, timestamp);
                        queue.offer(record);
                    } catch (DataException e) {
                        log.error("invalid aircraft data message={}, ignoring", struct);
                    }

                } else {
                    log.debug("aircraft {} not updated, skipping", icao24);
                }
            });

            lastTimestamp = maxTimestamp;

        } catch (final IOException e) {
            log.warn("exception reading from Opensky, ignoring and will try again.", e);
            first = false;
        } catch (final RuntimeException e) {
            log.warn("runtime exception reading from Opensky, ignoring and will try again.", e);
            first = false;
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        log.info("poll(), queue size = {}", queue.size());

        if (!first) {
            Thread.sleep(interval);
        }
        first = false;

        if (queue.isEmpty()) {
            getStates();
        }

        List<SourceRecord> result = new LinkedList<>();

        if (queue.isEmpty()) {
            // do not pause, try again immediately
            first = true;
        }

        queue.drainTo(result);

        log.info("poll(), result size = {}", result.size());

        return result;
    }

    @Override
    public void stop() {
        queue.clear();
    }
}
