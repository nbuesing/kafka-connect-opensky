package com.github.nbuesing.kafka.connect.opensky.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

class DurationParserTest {

  @ParameterizedTest(name = "parse simple : {0}")
  @MethodSource("durationProvider")
  void parseDuration(String value, Duration expected) {
    Assertions.assertEquals(expected, DurationParser.parse(value).get());
    Assertions.assertEquals(expected, DurationParser.parse(value.toUpperCase()).get());
  }

  static Stream<Arguments> durationProvider() {
    return Stream.of(
            Arguments.of("-1ns", Duration.ofNanos(-1)),
            Arguments.of("0ns", Duration.ofNanos(0)),
            Arguments.of("1ns", Duration.ofNanos(1)),
            Arguments.of("2ns", Duration.ofNanos(2)),
            Arguments.of("1us", Duration.of(1, ChronoUnit.MICROS)),
            Arguments.of("2us", Duration.of(2, ChronoUnit.MICROS)),
            Arguments.of("1ms", Duration.ofMillis(1)),
            Arguments.of("2ms", Duration.ofMillis(2)),
            Arguments.of("1s", Duration.ofSeconds(1)),
            Arguments.of("2s", Duration.ofSeconds(2)),
            Arguments.of("1h", Duration.ofHours(1)),
            Arguments.of("2h", Duration.ofHours(2)),
            Arguments.of("-1d", Duration.ofDays(-1)),
            Arguments.of("0d", Duration.ofDays(0)),
            Arguments.of("1d", Duration.ofDays(1)),
            Arguments.of("2d", Duration.ofDays(2))
    );
  }

  @ParameterizedTest(name = "parse iso : {0}")
  @ValueSource(strings = {"PT20.345S", "PT15M", "PT10H", "P2D"})
  void parseIsoDuration(String value) {
    Assertions.assertEquals(Duration.parse(value), DurationParser.parse(value).get());
  }

  @ParameterizedTest(name = "parse invalid : {0}")
  @ValueSource(strings = {"0x", "0Ns", "foo", "bar", "+-0s"})
  void parseInvalidDuration(String value) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> DurationParser.parse(value));
  }
}
