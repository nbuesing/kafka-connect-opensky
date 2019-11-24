package com.github.nbuesing.kafka.connect.opensky


import spock.lang.Specification

class OpenSkySourceConnectorSpec extends Specification {


    private OpenSkySourceConnector connector = new OpenSkySourceConnector()

    def taskConfigs() {

        when:
        connector.start([
                topic             : 'bar',
                interval          : '10',
                'bounding.boxes'  : '-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0'
        ])
        List<Map<String,String>> configs = connector.taskConfigs(maxTasks)

        then:
        assert configs.get(0).topic == 'bar'
        assert configs.size() == 2
        assert configs.get(0)['bounding.boxes'] == '-10.0 10.0 -20.0 0.0'
        assert configs.get(1)['bounding.boxes'] == '-10.0 10.0 0.0 20.0'

        where:
        maxTasks << [2, 3]
    }

    def taskConfigs2() {

        when:
        connector.start([
                topic             : 'bar',
                interval          : '10',
                'bounding.boxes'  : '-10.0 10.0 -20.0 0.0, -10.0 10.0 0.0 20.0'
        ])
        List<Map<String,String>> configs = connector.taskConfigs(1)

        then:
        assert configs.get(0).topic == 'bar'
        assert configs.size() == 1
        assert configs.get(0)['bounding.boxes'] == '-10.0 10.0 -20.0 0.0,-10.0 10.0 0.0 20.0'
    }
}
