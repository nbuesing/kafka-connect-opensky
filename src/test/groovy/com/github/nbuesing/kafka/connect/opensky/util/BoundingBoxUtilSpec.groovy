package com.github.nbuesing.kafka.connect.opensky.util

import com.github.nbuesing.kafka.connect.opensky.api.BoundingBox
import org.apache.kafka.common.config.ConfigException
import spock.lang.Specification

//import org.opensky.api.OpenSkyApi

import spock.lang.Unroll

@Unroll
class BoundingBoxUtilSpec extends Specification {

    def 'validate successfully : #boundingBox'() {

        when:
        BoundingBoxUtil.validate("foo", boundingBox)

        then:
        noExceptionThrown()

        where:
        boundingBox << ['-90 90 -180 180', '0 0 0 0', '-1 1 -1 1', ' -1\t1 -1\t1 ', '\t-1\t1\t-1\t1\t']
    }

    def 'validate unsuccessfully : #boundingBox'() {

        when:
        BoundingBoxUtil.validate("foo", boundingBox)

        then:
        thrown(ConfigException)

        where:
        boundingBox << ['-91 90 -180 180', '1 0 0 0', '-1 1 -1 -2', '-1 1 -1', 'foo', '', null]
    }

    def 'conversion : #property'() {

        when:
        BoundingBox boundingBox = BoundingBoxUtil.toBoundingBox(property)
        String string = BoundingBoxUtil.toString(boundingBox)

        then:
        assert string == property
        assert boundingBox.getMinLatitude() == property.split(' ')[0] as double
        assert boundingBox.getMaxLatitude() == property.split(' ')[1] as double
        assert boundingBox.getMinLongitude() == property.split(' ')[2] as double
        assert boundingBox.getMaxLongitude() == property.split(' ')[3] as double

        where:
        property << ['-90.0 90.0 -180.0 180.0', '0.0 0.0 0.0 0.0', '-1.0 1.0 -1.0 1.0']
    }

    def 'isWorld : #boundingBox'() {
        expect:
        assert BoundingBoxUtil.isWorld(BoundingBoxUtil.toBoundingBox(boundingBox)) == expected

        where:
        boundingBox                      | expected
        '-90 90 -180 180'                | true
        '-90.0 90.0 -180.0 180.0'        | true
        '-90.00 90.00 -180.00 180.00'    | true
        '-89.99999 90.00 -180.00 180.00' | false
        '0 0 0 0'                        | false
        '-1 1 -1 1'                      | false
        '-89.99 90    -180    180'       | false
        '-90    89.99 -180    180'       | false
        '-90    90    -179.99 180'       | false
        '-90    90    -180    179.00'    | false
    }
}
