package com.github.nbuesing.kafka.connect.opensky.converter

import com.github.nbuesing.kafka.connect.opensky.api.Record
import org.apache.kafka.connect.data.Struct
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RecordConverterSpec extends Specification {

    def 'convert'() {

        setup:
        long now = System.currentTimeMillis() / 1000L
        long nowMinus2 = now - 2L

        Record stateVector = new Record()
        stateVector.setIcao24('ICAO24')
        stateVector.setCallSign('CALLSIGN')
        stateVector.setLatitude(81.23)
        stateVector.setLongitude(145.67)
        stateVector.setBaroAltitude(32.1)
        stateVector.setGeoAltitude(43.2)
        stateVector.setVelocity(54.3)
        stateVector.setTrueTrack(65.4)
        stateVector.setOnGround(true)
        stateVector.setOriginCountry('USA')
        stateVector.setTimePosition(now)
        stateVector.setLastContact(nowMinus2)
        stateVector.setSpi(true)
        stateVector.setPositionSource(1)

        when:
        Struct struct = RecordConverter.convert(stateVector)

        then:
        assert struct.get('id') == 'ICAO24'
        assert struct.get('callsign') == 'CALLSIGN'
        assert (struct.get('location') as Struct).get('lat') == 81.23
        assert (struct.get('location') as Struct).get('lon') == 145.67
        assert struct.get('originCountry') == 'USA'
        assert struct.get('barometricAltitude') == 32.1
        assert struct.get('geometricAltitude') == 43.2
        assert struct.get('velocity') == 54.3
        assert struct.get('heading') == 65.4
        assert struct.get('onGround') == true
        assert struct.get('specialPurpose') == true
        assert struct.get('positionSource') == '1'
        assert struct.get('timePosition') == new Date(now * 1000L as Long)
        assert struct.get('lastContact') == new Date(nowMinus2 * 1000L as Long)
    }

    def 'convert - nulls'() {

        setup:

        Record stateVector = new Record()
        stateVector.setIcao24('ICAO24')
        stateVector.setLatitude(12.34)
        stateVector.setLongitude(167.89)

        when:
        Struct struct = RecordConverter.convert(stateVector)

        then:
        assert struct.get('id') == 'ICAO24'
        assert struct.get('callsign') == null
        assert (struct.get('location') as Struct).get('lat') == 12.34
        assert (struct.get('location') as Struct).get('lon') == 167.89
        assert struct.get('originCountry') == null
        assert struct.get('barometricAltitude') == null
        assert struct.get('geometricAltitude') == null
        assert struct.get('velocity') == null
        assert struct.get('heading') == null
        assert struct.get('onGround') == null
        assert struct.get('specialPurpose') == null
        assert struct.get('positionSource') == null
        assert struct.get('timePosition') == null
        assert struct.get('lastContact') == null
    }

}
