package com.github.nbuesing.kafka.connect.opensky.util;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

class ValidatorsTest {

//
//  // Validate successfully : boundingBoxes
//  @ParameterizedTest(name = "validate successfully : {0}")
//  @ValueSource(strings = {
//          List.of("-90 90 -180 180"),
//          "0 0 0 0",
//          "-1 1 -1 1"})
  void validateSuccessfullyBoundingBoxes(String boundingBox) {
    System.out.println(boundingBox);
    Assertions.assertDoesNotThrow(() -> Validators.validBoundingBoxes.ensureValid("foo", boundingBox));
  }


  // Validate successfully : topic
  @ParameterizedTest(name = "validate successfully : {0}")
  @ValueSource(strings = {"a", "a.b", "a_b", "a-b", "_a", ".a", "-a", "a6", "6a"})
  void validateSuccessfullyTopic(String topic) {
    Assertions.assertDoesNotThrow(() -> Validators.validTopic.ensureValid("foo", topic));
  }

  // Validate unsuccessfully : topic
  @ParameterizedTest(name = "validate unsuccessfully : {0}")
  @NullAndEmptySource
  @ValueSource(strings = {"$", "foo6%"})
  void validateUnsuccessfullyTopic(String topic) {
    Assertions.assertThrows(ConfigException.class, () -> Validators.validTopic.ensureValid("foo", topic));
  }


  @ParameterizedTest(name = "validate unsuccessfully : {0}")
  @MethodSource("boundingBoxesProvider")
  void validateUnsuccessfully(List<String> boundingBoxes) {
    Assertions.assertThrows(ConfigException.class,
            () -> Validators.validBoundingBoxes.ensureValid("foo", boundingBoxes));
  }

  private static Stream<Arguments> boundingBoxesProvider() {
    return Stream.of(
            List.of("-91 90 -180 180"),
            List.of("-90 90 -180 180", "-91 90 -180 180"),
            List.of("-91 90 -180 180", "-90 90 -180 180"),
            List.of("1 0 0 0"),
            List.of("-1 1 -1 -2")
    ).map(Arguments::of);
  }
}
