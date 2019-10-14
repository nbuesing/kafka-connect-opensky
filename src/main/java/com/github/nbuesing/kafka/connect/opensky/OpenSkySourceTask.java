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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OpenSkySourceTask extends SourceTask {

    private static final String BOUNDING_BOX = "boundingBox";
    private static final String TIMESTAMP = "timestamp";

    private BlockingQueue<SourceRecord> queue = null;
    private String topic = null;


    // epoc in seconds (not milliseconds)
    private long lastTimestamp;
    //private long maxTimestamp;

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


        final Map<String, String> sourcePartition = offsetKey(boundingBox);

        final Map<String, Object> currentSourceOffset = context.offsetStorageReader().offset(sourcePartition);

        lastTimestamp = getTimestamp(currentSourceOffset);

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

            final Map<String, Object> sourceOffset = offsetValue(os.getTime());

            log.info("Processing timestamp={}, numRecords={}, boundingBox={}", os.getTime(), os.getStates().size(), BoundingBoxUtil.toString(boundingBox));

            AtomicInteger skipped = new AtomicInteger();

            os.getStates().forEach(vector -> {

                final String icao24 = vector.getIcao24();

                if (vector.getLastContact() > lastTimestamp
                        && vector.getLatitude() != null
                        && vector.getLongitude() != null
                ) {

                    log.debug("aircraft icao24={}, timestamp={} updated, sending", icao24, vector.getLastContact());

                    final Struct struct = RecordConverter.convert(vector);

                    try {
                        struct.validate();

                        log.debug("aircraft transponder={}, timestamp={}", icao24, vector.getLastContact());

                        SourceRecord record = new SourceRecord(sourcePartition, sourceOffset, topic, null, RecordConverter.SCHEMA_KEY, vector.getIcao24().trim(), RecordConverter.SCHEMA, struct, vector.getLastContact() * 1000L);
                        queue.offer(record);
                    } catch (DataException e) {
                        log.error("invalid aircraft data message={}, ignoring", struct);
                    }

                } else {
                    skipped.incrementAndGet();
                    log.debug("aircraft icao24={}, timestamp={} not updated, skipping", icao24, vector.getLastContact());
                }
            });

            log.info("aircrafts fetched={}, skipped={}", os.getStates().size(), skipped.intValue());

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

    private Map<String, String> offsetKey(BoundingBox boundingBox) {
        return Collections.singletonMap(BOUNDING_BOX, BoundingBoxUtil.toString(boundingBox));
    }

    private Map<String, Object> offsetValue(final Integer timestamp) {
        return Collections.singletonMap(TIMESTAMP, timestamp);
    }

    private long getTimestamp(final Map<String, Object> offsetValue) {

        if (offsetValue == null) {
            return 0;
        }

        final Long value = (Long) offsetValue.get(TIMESTAMP);
        return (value != null) ? value : 0;
    }
}
