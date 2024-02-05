package com.github.nbuesing.kafka.connect.opensky;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenSkySourceConnectorTest {

  private OpenSkySourceConnector connector = new OpenSkySourceConnector();

  @ParameterizedTest
  @ValueSource(ints = {2, 3})
    // replace with your actual values
  void testTaskConfigs(int maxTasks) {
    connector.start(Map.of(
            "topic", "bar",
            "interval", "10",
            "bounding.boxes", "-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0"));

    List<Map<String, String>> configs = connector.taskConfigs(maxTasks);

    assertEquals("bar", configs.get(0).get("topic"));
    assertEquals(2, configs.size());
    assertEquals("-10.0 10.0 -20.0 0.0", configs.get(0).get("bounding.boxes"));
    assertEquals("-10.0 10.0 0.0 20.0", configs.get(1).get("bounding.boxes"));
  }

  @Test
  void testTaskConfigs2() {
    connector.start(Map.of(
            "topic", "bar",
            "interval", "10",
            "bounding.boxes", "-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0"));

    List<Map<String, String>> configs = connector.taskConfigs(1);

    assertEquals("bar", configs.get(0).get("topic"));
    assertEquals(1, configs.size());
    assertEquals("-10.0 10.0 -20.0 0.0,-10.0 10.0 0.0 20.0", configs.get(0).get("bounding.boxes"));
  }

}
