package com.github.nbuesing.kafka.connect.opensky.converter

import org.apache.kafka.connect.data.Struct
import org.opensky.model.StateVector
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StateVectorConverterSpec extends Specification {

    def 'convert'() {

        setup:
        long now = System.currentTimeMillis() / 1000L
        long nowMinus2 = now - 2L

        StateVector stateVector = new StateVector('ICAO24')
        stateVector.setCallsign('CALLSIGN')
        stateVector.setLatitude(81.23)
        stateVector.setLongitude(145.67)
        stateVector.setBaroAltitude(32.1)
        stateVector.setGeoAltitude(43.2)
        stateVector.setVelocity(54.3)
        stateVector.setHeading(65.4)
        stateVector.setOnGround(true)
        stateVector.setOriginCountry('USA')
        stateVector.setLastPositionUpdate(now)
        stateVector.setLastContact(nowMinus2)
        stateVector.setSpi(true)
        stateVector.setPositionSource(StateVector.PositionSource.ADS_B)

        when:
        Struct struct = StateVectorConverter.convert(stateVector)

        then:
        assert struct.get('icao24') == 'ICAO24'
        assert struct.get('callSign') == 'CALLSIGN'
        assert (struct.get('location') as Struct).get('latitude') == 81.23
        assert (struct.get('location') as Struct).get('longitude') == 145.67
        assert struct.get('originCountry') == 'USA'
        assert struct.get('barometricAltitude') == 32.1
        assert struct.get('geometricAltitude') == 43.2
        assert struct.get('velocity') == 54.3
        assert struct.get('heading') == 65.4
        assert struct.get('onGround') == true
        assert struct.get('specialPurpose') == true
        assert struct.get('positionSource') == 'ADS_B'
        assert struct.get('timePosition') == new Date(now * 1000L as Long)
        assert struct.get('lastContact') == new Date(nowMinus2 * 1000L as Long)
    }

    def 'convert - nulls'() {

        setup:

        StateVector stateVector = new StateVector('ICAO24')
        stateVector.setLatitude(12.34)
        stateVector.setLongitude(167.89)

        when:
        Struct struct = StateVectorConverter.convert(stateVector)

        then:
        assert struct.get('icao24') == 'ICAO24'
        assert struct.get('callSign') == null
        assert (struct.get('location') as Struct).get('latitude') == 12.34
        assert (struct.get('location') as Struct).get('longitude') == 167.89
        assert struct.get('originCountry') == null
        assert struct.get('barometricAltitude') == null
        assert struct.get('geometricAltitude') == null
        assert struct.get('velocity') == null
        assert struct.get('heading') == null
        assert struct.get('onGround') == false
        assert struct.get('specialPurpose') == false
        assert struct.get('positionSource') == null
        assert struct.get('timePosition') == null
        assert struct.get('lastContact') == null
    }

}
