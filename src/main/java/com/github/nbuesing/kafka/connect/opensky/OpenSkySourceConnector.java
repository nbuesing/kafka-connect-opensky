package com.github.nbuesing.kafka.connect.opensky;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpenSkySourceConnector extends SourceConnector {

    private OpenSkySourceConnectorConfig config;
    private Map<String, String> settings;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        config = new OpenSkySourceConnectorConfig(props);
        this.settings = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return OpenSkySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {

        final List<String> boundingBoxes = config.getList(OpenSkySourceConnectorConfig.BOUNDING_BOXES_CONF);

        log.info("taskConfigs({}) boundingBoxes={}", maxTasks, boundingBoxes.size());

        ArrayList<Map<String, String>> configs = new ArrayList<>();

        final int tasks = Math.min(maxTasks, boundingBoxes.size());

//        for (List<String> workUnit : Iterables.partition(boundingBoxes, tasks)) {
        for (List<String> workUnit : ListUtils.partition(boundingBoxes, tasks)) {
            Map<String, String> config = new HashMap<>(settings);

            if (!workUnit.isEmpty()) {
                config.put(OpenSkySourceConnectorConfig.BOUNDING_BOXES_CONF, String.join(",", workUnit));
                configs.add(config);
            }
        }

        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return OpenSkySourceConnectorConfig.conf();
    }

}

