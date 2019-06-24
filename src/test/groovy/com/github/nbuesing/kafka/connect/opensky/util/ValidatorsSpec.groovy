package com.github.nbuesing.kafka.connect.opensky.util

import org.apache.kafka.common.config.ConfigException
import spock.lang.Specification

class ValidatorsSpec extends Specification {

    def 'validate successfully : #boundingBoxes'() {

        when:
        Validators.validBoundingBoxes.ensureValid('foo', boundingBoxes)

        then:
        noExceptionThrown()

        where:
        boundingBoxes << [
                null,
                [],
                ['-90 90 -180 180'],
                ['-90 90 -180 180', '0 0 0 0'],
                ['0 0 0 0'],
                ['-1 1 -1 1']
        ]
    }

    def 'validate unsuccessfully : #boundingBoxes'() {

        when:
        Validators.validBoundingBoxes.ensureValid('foo', boundingBoxes)

        then:
        thrown(ConfigException)

        where:
        boundingBoxes << [
                ['-91 90 -180 180'],
                ['-90 90 -180 180', '-91 90 -180 180'],
                ['-91 90 -180 180', '-90 90 -180 180'],
                ['1 0 0 0'],
                ['-1 1 -1 -2']
        ]
    }
}
