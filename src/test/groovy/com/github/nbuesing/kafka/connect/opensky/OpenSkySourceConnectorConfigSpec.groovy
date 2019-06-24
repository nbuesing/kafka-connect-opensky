package com.github.nbuesing.kafka.connect.opensky

import org.apache.kafka.common.config.ConfigException
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class OpenSkySourceConnectorConfigSpec extends Specification {

    def 'empty set of properties'() {

        setup:
        Map<String, String> props = [:]

        when:
        new OpenSkySourceConnectorConfig(props)

        then:
        ConfigException e = thrown(ConfigException)
        assert e.getMessage().contains('"topic"')
    }

    def 'minimum set of properties'() {

        setup:
        Map<String, String> props = [
                topic: 'foo'
        ]

        when:
        OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props)

        then:
        assert config.getTopic() == 'foo'
    }

    def 'set all properties'() {

        setup:
        Map<String, String> props = [
                topic             : 'foo',
                'bounding.boxes'  : '-90 90 -180 180',
                interval          : 30,
                'opensky.username': 'user',
                'opensky.password': 'password'
        ]

        when:
        OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props)

        then:
        assert config.getTopic() == 'foo'
        assert config.getInterval().get() == 30L
        assert config.getOpenskyUsername().get() == 'user'
        assert config.getOpenskyPassword().get() == 'password'
    }

    def 'bounding box : #boundingBox'() {

        setup:
        Map<String, String> props = [
                topic           : 'foo',
                'bounding.boxes': boundingBox
        ]

        when:
        OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props)

        then:
        assert config.getBoundingBoxes().size() == 1
        assert config.getBoundingBoxes().get(0).getMinLatitude() == minLat as double
        assert config.getBoundingBoxes().get(0).getMaxLatitude() == maxLat as double
        assert config.getBoundingBoxes().get(0).getMinLongitude() == minLong as double
        assert config.getBoundingBoxes().get(0).getMaxLongitude() == maxLong as double

        where:
        boundingBox                     | minLat | maxLat | minLong | maxLong
        '-90 90 -180 180'               | -90.0  | 90.0   | -180.0  | 180.0
        '-90.0 90.00 -180.000 180.0000' | -90.0  | 90.0   | -180.0  | 180.0
        '-1.2 3.45 6.789 10.123'        | -1.2   | 3.45   | 6.789   | 10.123
    }

    def 'invalid bounding box : #boundingBox'() {

        setup:
        Map<String, String> props = [
                topic           : 'foo',
                'bounding.boxes': boundingBox
        ]

        when:
        new OpenSkySourceConnectorConfig(props)

        then:
        ConfigException e = thrown(ConfigException)

        assert e.getMessage().contains(message)

        where:
        boundingBox         | message
        '-90.1 90 -180 180' | 'latitude=-90.1'
        '-90 90.1 -180 180' | 'latitude=90.1'
        '-90 90 -180.1 180' | 'longitude=-180.1'
        '-90 90 -180 180.1' | 'longitude=180.1'
        '-45 -46 -180 180'  | 'min=-45.0 max=-46.0'
        '-90 90 55 54.9'    | 'min=55.0 max=54.9'
    }

    def 'bounding boxes'() {

        setup:
        Map<String, String> props = [
                topic           : 'foo',
                'bounding.boxes': "-10 10 -20 20 , -5 5 -2 2"
        ]

        when:
        OpenSkySourceConnectorConfig config = new OpenSkySourceConnectorConfig(props)

        then:
        assert config.getBoundingBoxes().size() == 2
        assert config.getBoundingBoxes().get(0).getMinLatitude() == -10
        assert config.getBoundingBoxes().get(0).getMaxLatitude() == 10
        assert config.getBoundingBoxes().get(0).getMinLongitude() == -20
        assert config.getBoundingBoxes().get(0).getMaxLongitude() == 20
        assert config.getBoundingBoxes().get(1).getMinLatitude() == -5
        assert config.getBoundingBoxes().get(1).getMaxLatitude() == 5
        assert config.getBoundingBoxes().get(1).getMinLongitude() == -2
        assert config.getBoundingBoxes().get(1).getMaxLongitude() == 2
    }

    //OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(45.8389, 47.8229, 5.9962, 10.5226));
    //OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(24.396308, 49.384358, -124.848974, -66.885444));

}
