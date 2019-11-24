package com.github.nbuesing.kafka.connect.opensky


import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTaskContext
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

@Unroll
class OpenSkySourceTaskSpec extends Specification {

    private OpenSkySourceTask task = new OpenSkySourceTask()

    @Rule
    private WireMockRule api = new WireMockRule(9999);


    @Shared
    private Map<String, String> config = [
            topic: 'foo'
    ]

    def 'config defaults'() {

        when:
        task.start(config)

        then:
        assert task.queue != null
        assert task.topic == 'foo'
        assert task.interval == 30000L
        assert task.url == 'https://opensky-network.org/api/'
        assert task.username == null
        assert task.password == null
        assert task.boundingBoxes == [BoundingBoxUtil.toBoundingBox('-90.0 90.0 -180.0 180.0')]

    }

    def 'config non-defaults'() {

        when:
        task.start([
                topic             : 'bar',
                interval          : '10',
                'opensky.url'     : url,
                'opensky.username': username,
                'opensky.password': password,
                'bounding.boxes'  : '-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0'
        ])

        then:
        assert task.queue != null
        assert task.topic == 'bar'
        assert task.interval == 10000L
        assert task.url == ((url == null) ? null : (url.endsWith('/') ? url : (url + '/')))
        assert task.username == username
        assert task.password == password
        assert task.boundingBoxes == [
                BoundingBoxUtil.toBoundingBox('-10.0 10.0 -20.0 0.0'),
                BoundingBoxUtil.toBoundingBox('-10.0 10.0 0.0 20.0')
        ]

        where:
        url                      | username | password
        null                     | null     | null
        'http://localhost:9999/' | 'U'      | 'P'
        'http://localhost:9999'  | 'U'      | 'P'
    }


    def 'start()'() {

        setup:

        Map<String, String> offset = task.offsetKey(BoundingBoxUtil.toBoundingBox('-90 90 -180 180'))

        OffsetStorageReader offsetStorageReader = Mock()
        SourceTaskContext context = Mock()

        task.context = context

        api.stubFor(
                get(urlMatching('/states/all.*'))
                        .inScenario("scenario1")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse().withBody('''
                            { 
                              "time": "1570243170",
                              "states": [
                                ["ab1644","UAL841  ","United States",1570243169,1570243169,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                                ["ac96b8","AAL1305 ","United States",1570243168,1570243169,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                              ]
                            }
                            '''
                        )).willSetStateTo("second")
        )

        api.stubFor(
                get(urlMatching('/states/all.*'))
                        .inScenario("scenario1")
                        .whenScenarioStateIs("second")
                        .willReturn(aResponse().withBody('''
                            { 
                              "time": "1570243180",
                              "states": [
                                ["ab1644","UAL841  ","United States",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                                ["ac96b8","AAL1305 ","United States",1570243178,1570243179,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                              ]
                            }
                            '''
                        )).willSetStateTo("third")
        )

        api.stubFor(
                get(urlMatching('/states/all.*'))
                        .inScenario("scenario1")
                        .whenScenarioStateIs("third")
                        .willReturn(aResponse().withBody('''
                            { 
                              "time": "1570243190",
                              "states": [
                                ["ab1644","UAL841  ","United States",1570243179,1570243179,-87.8617,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                                ["ac96b8","AAL1305 ","United States",1570243188,1570243189,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                              ]
                            }
                            '''
                        ))
        )

        when:
        task.start([
                topic        : 'bar',
                interval     : '1',
                'opensky.url': 'http://localhost:9999'
        ])

        List<SourceRecord> first = task.poll()
        List<SourceRecord> second = task.poll()
        List<SourceRecord> third = task.poll()

        task.stop()

        then:
        assert first.size() == 2
        assert second.size() == 2
        assert third.size() == 1
        //  assert fourth.size() == 0

        context.offsetStorageReader() >> offsetStorageReader
        offsetStorageReader.offset(offset) >>> [
                null,
                task.offsetValue(1570243170),
                task.offsetValue(1570243180)
        ]
        0 * _

    }

    def 'start() - bad data'() {

        setup:

        Map<String, String> offset = task.offsetKey(BoundingBoxUtil.toBoundingBox('-90 90 -180 180'))

        OffsetStorageReader offsetStorageReader = Mock()
        SourceTaskContext context = Mock()

        task.context = context

        api.stubFor(
                get(urlMatching('/states/all.*'))
                        .willReturn(aResponse().withBody('''
                            { 
                              "time": "1570243170",
                              "states": [
                                ["ab1644","UAL841","United States",1570243169,1570243169,null,40.2516,10050.78,false,246.08,177.84,1.63,null,10568.94,"5102",false,0],
                                ["ac96b8","AAL1305 ","United States",1570243168,1570243169,-89.3563,32.4248,11582.4,false,236.75,95.61,0,null,12214.86,null,false,0]
                              ]
                            }
                            '''
                        ))
        )

        when:
        task.start([
                topic        : 'bar',
                interval     : '1',
                'opensky.url': 'http://localhost:9999'
        ])

        List<SourceRecord> records = task.poll()

        task.stop()

        then:
        assert records.size() == 1
        context.offsetStorageReader() >> offsetStorageReader
        offsetStorageReader.offset(offset) >> null
        0 * _

    }

}
