package com.github.nbuesing.kafka.connect.opensky;

import com.github.nbuesing.kafka.connect.opensky.api.BoundingBox;
import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OpenSkySourceTaskTest {

  @RegisterExtension
  private static WireMockExtension api = WireMockExtension.newInstance().options(wireMockConfig().port(9999)).build();

  private final OpenSkySourceTask task = new OpenSkySourceTask();

  private Map<String, String> config = Map.ofEntries(
          Map.entry("topic", "foo")
  );

  @Test
  void configDefaults() {
    task.start(config);
    assertNotNull(task.getQueue());
    Assertions.assertEquals("foo", task.getTopic());
    Assertions.assertEquals(30000L, task.getInterval());
    Assertions.assertEquals("https://opensky-network.org/api/", task.getUrl());
    assertNull(task.getUsername());
    assertNull(task.getPassword());
    Assertions.assertEquals(List.of(BoundingBoxUtil.toBoundingBox("-90.0 90.0 -180.0 180.0")), task.getBoundingBoxes());
  }


  @ParameterizedTest(name = "config non-defaults, url={0}")
  @MethodSource("provideConfigParameters")
  void configNonDefaults(String url, String username, String password) {
    Map<String, String> params = new HashMap<>();
    params.put("topic", "bar");
    params.put("interval", "10");
    params.put("opensky.url", url);
    params.put("opensky.username", username);
    params.put("opensky.password", password);
    params.put("bounding.boxes", "-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0");

    task.start(params);

    if (url == null) {
      assertNull(task.getUrl());
    } else if (url.endsWith("/")) {
      assertEquals(url, task.getUrl());
    } else {
      assertEquals(url + "/", task.getUrl());
    }
    assertEquals(username, task.getUsername());
    assertEquals(password, task.getPassword());
    assertEquals(List.of(
                    BoundingBoxUtil.toBoundingBox("-10.0 10.0 -20.0 0.0"),
                    BoundingBoxUtil.toBoundingBox("-10.0 10.0 0.0 20.0")),
            task.getBoundingBoxes());
  }

  private static Stream<Arguments> provideConfigParameters() {
    return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("http://localhost:9999/", "U", "P"),
            Arguments.of("http://localhost:9999", "U", "P")
    );
  }

  @Test
  void testSetup() throws Exception {

    Method method = OpenSkySourceTask.class.getDeclaredMethod("offsetKey", BoundingBox.class);
    method.setAccessible(true);
    method.invoke(task, BoundingBoxUtil.toBoundingBox("-90 90 -180 180"));

    SourceTaskContext context = Mockito.mock(SourceTaskContext.class);
    OffsetStorageReader offsetStorageReader = Mockito.mock(OffsetStorageReader.class);

    Field field = SourceTask.class.getDeclaredField("context");
    field.setAccessible(true);

    field.set(task, context);

    Mockito.when(context.offsetStorageReader()).thenReturn(offsetStorageReader);

    Mockito.when(offsetStorageReader.offset(Mockito.any()))
            .thenReturn(
                    null
            )
            .thenReturn(
                    Collections.singletonMap("timestamp", 1570243170L)
            ).thenReturn(
                    Collections.singletonMap("timestamp", 1570243180L)
            );

    api.stubFor(
            get(urlMatching("/states/all.*"))
                    .inScenario("scenario1")
                    .whenScenarioStateIs(Scenario.STARTED)
                    .willReturn(aResponse().withBody("{" +
                            "  \"time\": \"1570243170\"," +
                            "        \"states\": [" +
                            "                            [\"ab1644\",\"UAL841  \",\"United States\",1570243169,1570243169,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,\"5102\",false,0]," +
                            "                            [\"ac96b8\",\"AAL1305 \",\"United States\",1570243168,1570243169,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]" +
                            "                          ]" +
                            "}"
                    )).willSetStateTo("second")
    );

    api.stubFor(
            get(urlMatching("/states/all.*"))
                    .inScenario("scenario1")
                    .whenScenarioStateIs("second")
                    .willReturn(aResponse().withBody("    {" +
                            "  \"time\": \"1570243180\"," +
                            "        \"states\": [" +
                            "                            [\"ab1644\",\"UAL841  \",\"United States\",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,\"5102\",false,0]," +
                            "                            [\"ac96b8\",\"AAL1305 \",\"United States\",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]" +
                            "                          ]" +
                            "}"
                    )).willSetStateTo("third")
    );

    api.stubFor(
            get(urlMatching("/states/all.*"))
                    .inScenario("scenario1")
                    .whenScenarioStateIs("third")
                    .willReturn(aResponse().withBody("{" +
                            "  \"time\": \"1570243190\"," +
                            "        \"states\": [" +
                            "                            [\"ab1644\",\"UAL841  \",\"United States\",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,\"5102\",false,0]," +
                            "                            [\"ac96b8\",\"AAL1305 \",\"United States\",1570243188,1570243189,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]" +
                            "                          ]" +
                            "}"
                    ))
    );

    task.start(Map.ofEntries(
            Map.entry("topic", "bar"),
            Map.entry("interval", "1"),
            Map.entry("opensky.url", "http://localhost:9999")
    ));

    List<SourceRecord> first = task.poll();
    List<SourceRecord> second = task.poll();
    List<SourceRecord> third = task.poll();

    task.stop();

    Assertions.assertEquals(2, first.size());
    Assertions.assertEquals(2, second.size());
    Assertions.assertEquals(1, third.size());
  }
}
