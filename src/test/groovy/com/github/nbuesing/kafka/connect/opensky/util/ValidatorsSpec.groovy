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


    def 'validate successfully : #topic'() {

        when:
        Validators.validTopic.ensureValid('foo', topic)

        then:
        noExceptionThrown()

        where:
        topic << [
                'a',
                'a.b',
                'a_b',
                'a-b',
                '_a',
                '.a',
                '-a',
                'a6',
                '6a'
        ]
    }

    def 'validate unsuccessfully : #topic'() {

        when:
        Validators.validTopic.ensureValid('foo', topic)

        then:
        thrown(ConfigException)

        where:
        topic << [
                null,
                '',
                '$',
                'foo6%'
        ]
    }
}
