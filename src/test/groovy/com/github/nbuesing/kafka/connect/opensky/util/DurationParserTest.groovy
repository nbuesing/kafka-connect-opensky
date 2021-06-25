package com.github.nbuesing.kafka.connect.opensky.util

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.temporal.ChronoUnit

@Unroll
class DurationParserTest extends Specification {

    def 'parse simple : #value'() {

        expect:
        assert DurationParser.parse(value).get() == expected
        assert DurationParser.parse(value.toUpperCase()).get() == expected

        where:
        value  | expected
        '-1ns' | Duration.ofNanos(-1)
        '0ns'  | Duration.ofNanos(0)
        '1ns'  | Duration.ofNanos(1)
        '2ns'  | Duration.ofNanos(2)
        '1us'  | Duration.of(1, ChronoUnit.MICROS)
        '2us'  | Duration.of(2, ChronoUnit.MICROS)
        '1ms'  | Duration.ofMillis(1)
        '2ms'  | Duration.ofMillis(2)
        '1s'   | Duration.ofSeconds(1)
        '2s'   | Duration.ofSeconds(2)
        '1h'   | Duration.ofSeconds(3600)
        '2h'   | Duration.ofSeconds(7200)
        '-1d'  | Duration.ofDays(-1)
        '0d'   | Duration.ofDays(0)
        '1d'   | Duration.ofDays(1)
        '2d'   | Duration.ofDays(2)
    }

    //
    // the underlying implementation is Duration.parse() for ISO standard, but nothing requires it to do so
    // and we have to make sure the checks between simple and iso do not cause something to fail
    def 'parse iso : #value'() {

        expect:
        assert DurationParser.parse(value).get() == Duration.parse(value)

        where:
        value << ['PT20.345S', 'PT15M', 'PT10H', 'P2D']
    }

    def 'parse invalid : #value'() {

        when:
        DurationParser.parse(value)

        then:
        thrown(IllegalArgumentException)

        where:
        value << ['0x', '0Ns', 'foo', 'bar', '+-0s']
    }

}
