package com.github.nbuesing.kafka.connect.opensky.converter

import spock.lang.Specification

class StateVectorFieldSpec extends Specification {

    def 'getters'() {
        expect:
        assert StateVectorField.CALL_SIGN.position == 1
        assert StateVectorField.CALL_SIGN.field == 'callSign'
        assert StateVectorField.CALL_SIGN.openskyField == 'call_sign'
        assert StateVectorField.CALL_SIGN.doc == 'Callsign of the vehicle (8 chars). Can be null if no callsign has been received.'
    }
}
