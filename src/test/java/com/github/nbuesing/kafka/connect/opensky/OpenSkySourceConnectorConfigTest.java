package com.github.nbuesing.kafka.connect.opensky;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenSkySourceConnectorConfigTest {

  @Test
  void testEmptySetOfProperties() {
    final Map<String, String> props = new HashMap<>();
    Exception exception = assertThrows(ConfigException.class, () -> new OpenSkySourceConnectorConfig(props));
    assertTrue(exception.getMessage().contains("\"topic\""));
  }

  @Test
  void testMinimumSetOfProperties() {
    final Map<String, String> props = new HashMap<>();
    props.put("topic", "foo");

    OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props);

    assertEquals("foo", config.getTopic());
  }

  @Test
  void testSetAllProperties() {
    Map<String, String> props = new HashMap<>();
    props.put("topic", "foo");
    props.put("bounding.boxes", "-90 90 -180 180");
    props.put("interval", "30");
    props.put("opensky.username", "user");
    props.put("opensky.password", "password");

    OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props);

    assertEquals("foo", config.getTopic());
    assertEquals(Long.valueOf(30), config.getInterval().get());
    assertEquals("user", config.getOpenskyUsername().get());
    assertEquals("password", config.getOpenskyPassword().get());
  }


  @ParameterizedTest
  @MethodSource("provideBoundingBoxes")
  void testBoundingBox(String boundingBox, double minLat, double maxLat, double minLong, double maxLong) {
    Map<String, String> props = new HashMap<>();
    props.put("topic", "foo");
    props.put("bounding.boxes", boundingBox);

    OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props);

    assertEquals(1, config.getBoundingBoxes().size());
    assertEquals(minLat, config.getBoundingBoxes().get(0).getMinLatitude());
    assertEquals(maxLat, config.getBoundingBoxes().get(0).getMaxLatitude());
    assertEquals(minLong, config.getBoundingBoxes().get(0).getMinLongitude());
    assertEquals(maxLong, config.getBoundingBoxes().get(0).getMaxLongitude());
  }

  private static Stream<Arguments> provideBoundingBoxes() {
    return Stream.of(
            Arguments.of("-90 90 -180 180", -90.0, 90.0, -180.0, 180.0),
            Arguments.of("-90.0 90.00 -180.000 180.0000", -90.0, 90.0, -180.0, 180.0),
            Arguments.of("-1.2 3.45 6.789 10.123", -1.2, 3.45, 6.789, 10.123)
    );
  }


  @ParameterizedTest
  @MethodSource("provideInvalidBoundingBoxes")
  void testInvalidBoundingBox(String boundingBox, String message) {
    Map<String, String> props = new HashMap<>();
    props.put("topic", "foo");
    props.put("bounding.boxes", boundingBox);

    Exception exception = assertThrows(ConfigException.class, () -> new OpenSkySourceConnectorConfig(props));

    assertTrue(exception.getMessage().contains(message));
  }

  private static Stream<Arguments> provideInvalidBoundingBoxes() {
    return Stream.of(
            Arguments.of("-90.1 90 -180 180", "latitude=-90.1"),
            Arguments.of("-90 90.1 -180 180", "latitude=90.1"),
            Arguments.of("-90 90 -180.1 180", "longitude=-180.1"),
            Arguments.of("-90 90 -180 180.1", "longitude=180.1"),
            Arguments.of("-45 -46 -180 180", "min=-45.0 max=-46.0"),
            Arguments.of("-90 90 55 54.9", "min=55.0 max=54.9")
    );
  }

  @Test
  void testBoundingBoxes() {
    Map<String, String> props = new HashMap<>();
    props.put("topic", "foo");
    props.put("bounding.boxes", "-10 10 -20 20 , -5 5 -2 2");

    OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props);

    assertEquals(2, config.getBoundingBoxes().size());
    assertEquals(-10, config.getBoundingBoxes().get(0).getMinLatitude());
    assertEquals(10, config.getBoundingBoxes().get(0).getMaxLatitude());
    assertEquals(-20, config.getBoundingBoxes().get(0).getMinLongitude());
    assertEquals(20, config.getBoundingBoxes().get(0).getMaxLongitude());
    assertEquals(-5, config.getBoundingBoxes().get(1).getMinLatitude());
    assertEquals(5, config.getBoundingBoxes().get(1).getMaxLatitude());
    assertEquals(-2, config.getBoundingBoxes().get(1).getMinLongitude());
    assertEquals(2, config.getBoundingBoxes().get(1).getMaxLongitude());
  }

}
