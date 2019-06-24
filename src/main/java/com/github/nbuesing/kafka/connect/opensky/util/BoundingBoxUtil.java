package com.github.nbuesing.kafka.connect.opensky.util;

import org.apache.kafka.common.config.ConfigException;
import org.opensky.api.OpenSkyApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BoundingBoxUtil {

    private static final String SEP = "\\s+";
    private static final String COORDINATE = "([-+]?[\\d]{1,3}(?:\\.\\d*)?)";
    private static final Pattern BOUNDING_BOX_PATTERN = Pattern.compile("\\s*" + COORDINATE + SEP + COORDINATE + SEP + COORDINATE + SEP + COORDINATE + "\\s*");

    private BoundingBoxUtil() {
    }

    public static boolean isWorld(final OpenSkyApi.BoundingBox boundingBox) {
        return boundingBox.getMinLatitude() == -90.0
                && boundingBox.getMaxLatitude() == 90.0
                && boundingBox.getMinLongitude() == -180.0
                && boundingBox.getMaxLongitude() == 180.0;
    }

    public static void validate(final String property, final String string) {

        if (string == null) {
            throw new ConfigException(property, null, "invalid bounding box.");
        }

        Matcher matcher = BOUNDING_BOX_PATTERN.matcher(string);

        if (!matcher.matches()) {
            throw new ConfigException(property, string, "invalid bounding box.");
        }

        double minLatitude = Double.parseDouble(matcher.group(1));
        double maxLatitude = Double.parseDouble(matcher.group(2));
        double minLongitude = Double.parseDouble(matcher.group(3));
        double maxLongitude = Double.parseDouble(matcher.group(4));

        checkLatitude(property, minLatitude);
        checkLatitude(property, maxLatitude);
        checkLongitude(property, minLongitude);
        checkLongitude(property, maxLongitude);
        checkMinMax(property, minLatitude, maxLatitude);
        checkMinMax(property, minLongitude, maxLongitude);
    }

    /**
     * Assumes the string has been validated when the configuration was loaded, so
     * allowing runtime exceptions of OpenSky API to be returned, as they would be
     * due to a program error not properly validating the coodinates at startup.
     */
    public static OpenSkyApi.BoundingBox toBoundingBox(final String string) {

        Matcher matcher = BOUNDING_BOX_PATTERN.matcher(string);

        if (!matcher.matches()) {
            throw new RuntimeException("invalid bounding box string, string=" + string);
        }

        return new OpenSkyApi.BoundingBox(
                Double.parseDouble(matcher.group(1)),
                Double.parseDouble(matcher.group(2)),
                Double.parseDouble(matcher.group(3)),
                Double.parseDouble(matcher.group(4))
        );
    }

    /**
     * Turn a BoundingBox back into the string representation used in configuration files.
     */
    public static String toString(OpenSkyApi.BoundingBox boundingBox) {
        return new StringBuilder()
                .append(boundingBox.getMinLatitude())
                .append(" ")
                .append(boundingBox.getMaxLatitude())
                .append(" ")
                .append(boundingBox.getMinLongitude())
                .append(" ")
                .append(boundingBox.getMaxLongitude())
                .toString();
    }

    private static void checkLatitude(final String property, double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new ConfigException(property, "latitude=" + latitude, "must be within [-90,90]");
        }
    }

    private static void checkLongitude(final String property, double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new ConfigException(property, "longitude=" + longitude, "must be within [-180,180]");
        }
    }

    private static void checkMinMax(final String property, double min, double max) {
        if (min > max) {
            throw new ConfigException(property, "min=" + min + " max=" + max, "min cannot be greater than max.");
        }
    }
}
