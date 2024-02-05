package com.github.nbuesing.kafka.connect.opensky.api;

import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenSkyTest {

  @RegisterExtension
  static WireMockExtension api = WireMockExtension.newInstance().options(wireMockConfig().port(9999)).build();

  @ParameterizedTest(name="getByBoundingBox user={0}, password={1}")
  @CsvSource({",", "foo,bar"})
  void getByBoundingBox(String username, String password) throws IOException {

    api.stubFor(
            get(urlEqualTo("/states/all?lamin=-89.0&lamax=90.0&lomin=-180.0&lomax=180.0"))
                    .willReturn(aResponse().withBody("{\n" +
                            "  \"time\": \"1570243180\",\n" +
                            "  \"states\": [\n" +
                            "    [\"ab1644\",\"UAL841  \",\"United States\",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,\"5102\",false,0],\n" +
                            "    [\"ac96b8\",\"AAL1305 \",\"United States\",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]\n" +
                            "  ]\n" +
                            "}")
                    ));

    OpenSky openSky = new OpenSky("http://localhost:9999", username, password, Optional.empty(), Optional.empty(), Optional.empty());

    Records records = openSky.getAircrafts(BoundingBoxUtil.toBoundingBox("-89 90 -180 180"));

    assertEquals(2, records.getStates().size());
    assertEquals(1570243180, records.getTime());
    assertEquals("ab1644", records.getStates().get(0).getIcao24());
    assertEquals("UAL841", records.getStates().get(0).getCallSign());


  }


  @Test
  void getAll() throws IOException {

    api.stubFor(
            get(urlEqualTo("/states/all"))
                    .willReturn(aResponse().withBody("{\n" +
                            "  \"time\": \"1570243180\",\n" +
                            "  \"states\": [\n" +
                            "    [\"ab1644\",\"UAL841  \",\"United States\",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,\"5102\",false,0],\n" +
                            "    [\"ac96b8\",\"AAL1305 \",\"United States\",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]\n" +
                            "  ]\n" +
                            "}")
                    ));

    OpenSky openSky = new OpenSky("http://localhost:9999", null, null, Optional.empty(), Optional.empty(), Optional.empty());

    Records records = openSky.getAircrafts(BoundingBoxUtil.toBoundingBox("-90 90 -180 180"));

    assertEquals(2, records.getStates().size());
    assertEquals(1570243180, records.getTime());
    assertEquals("ab1644", records.getStates().get(0).getIcao24());
    assertEquals("UAL841", records.getStates().get(0).getCallSign());

    assertEquals(2, openSky.getAircrafts(null).getStates().size());
  }



}
