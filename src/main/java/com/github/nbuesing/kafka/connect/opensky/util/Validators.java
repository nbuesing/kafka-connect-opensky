package com.github.nbuesing.kafka.connect.opensky.util;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.List;
import java.util.regex.Pattern;

public final class Validators {

    private static final Pattern TOPIC_NAME = Pattern.compile("[a-zA-Z0-9\\._\\-]{1,255}");

    private Validators() {
    }

    @SuppressWarnings("unchecked")
    public static ConfigDef.Validator validBoundingBoxes = (name, value) -> {
        if (value == null) {
            return;
        }
        ((List<String>) value).forEach(string -> {
            BoundingBoxUtil.validate(name, string);
        });
    };


    public static ConfigDef.Validator validTopic = (name, value) -> {
        if (value == null) {
            throw new ConfigException(name, null, "Value cannot be null.");
        }

        if (!TOPIC_NAME.matcher((String) value).matches()) {
            throw new ConfigException(name, value, "Value can only contain alphanumeric, '.', '_', or '-' and be less than 255 characters.");
        }
    };

}
