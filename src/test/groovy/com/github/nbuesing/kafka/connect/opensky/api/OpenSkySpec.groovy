package com.github.nbuesing.kafka.connect.opensky.api

import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Unroll
class OpenSkySpec extends Specification {

    @Rule
    private WireMockRule api = new WireMockRule(9999);

    def 'get by bounding box'() {
        setup:

        api.stubFor(
                get(urlEqualTo('/states/all?lamin=-89.0&lamax=90.0&lomin=-180.0&lomax=180.0'))
                        .willReturn(aResponse().withBody('''
                { 
                  "time": "1570243180",
                  "states": [
                    ["ab1644","UAL841  ","United States",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                    ["ac96b8","AAL1305 ","United States",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                  ]
                }
                '''
                        )))

        OpenSky openSky = new OpenSky("http://localhost:9999", username, password, Optional.empty(), Optional.empty(), Optional.empty())

        when:
        Records records = openSky.getAircrafts(BoundingBoxUtil.toBoundingBox('-89 90 -180 180'));

        then:
        assert records.getStates().size() == 2
        assert records.getTime() == 1570243180
        assert records.getStates().get(0).getIcao24() == 'ab1644'
        assert records.getStates().get(0).getCallSign() == 'UAL841'

        where:
        username | password
        null     | null
        'foo'    | 'bar'
    }

    def 'get all'() {
        setup:

        api.stubFor(
                get(urlEqualTo('/states/all'))
                        .willReturn(aResponse().withBody('''
                { 
                  "time": "1570243180",
                  "states": [
                    ["ab1644","UAL841  ","United States",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                    ["ac96b8","AAL1305 ","United States",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                  ]
                }
                '''
                        )))

        OpenSky openSky = new OpenSky("http://localhost:9999", null, null, Optional.empty(), Optional.empty(), Optional.empty())

        when:
        Records records = openSky.getAircrafts(boundingBox);

        then:
        assert records.getStates().size() == 2
        assert records.getTime() == 1570243180
        assert records.getStates().get(0).getIcao24() == 'ab1644'
        assert records.getStates().get(0).getCallSign() == 'UAL841'

        where:
        boundingBox << [
                BoundingBoxUtil.toBoundingBox('-90 90 -180 180'),
                null
        ]
    }
}
