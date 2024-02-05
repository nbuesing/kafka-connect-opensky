package com.github.nbuesing.kafka.connect.opensky.util;

import com.github.nbuesing.kafka.connect.opensky.api.BoundingBox;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoundingBoxUtilTest {


  // validate successfully : boundingBox
  @ParameterizedTest(name = "validate successfully : {0}")
  @ValueSource(strings = {"-90 90 -180 180", "0 0 0 0", "-1 1 -1 1", " -1\t1 -1\t1 ", "\t-1\t1\t-1\t1\t"})
  void validateSuccessfully(String boundingBox) {
    assertDoesNotThrow(() -> BoundingBoxUtil.validate("foo", boundingBox));
  }

  // validate unsuccessfully : boundingBox
  @ParameterizedTest(name = "validate unsuccessfully : {0}")
  @NullAndEmptySource
  @ValueSource(strings = {"-91 90 -180 180", "1 0 0 0", "-1 1 -1 -2", "-1 1 -1", "foo"})
  void validateUnsuccessfully(String boundingBox) {
    assertThrows(ConfigException.class, () -> BoundingBoxUtil.validate("foo", boundingBox));
  }

  // invalid bounding box
  @ParameterizedTest(name = "invalid bounding box : {0}")
  @ValueSource(strings = {"-90.0", "a.0 0.0 0.0 0.0", "-1.0 1.0"})
  void invalidBoundingBox(String property) {
    assertThrows(RuntimeException.class, () -> BoundingBoxUtil.toBoundingBox(property));
  }

  // conversion : property
  @ParameterizedTest(name = "conversion : {0}")
  @ValueSource(strings = {"-90.0 90.0 -180.0 180.0", "0.0 0.0 0.0 0.0", "-1.0 1.0 -1.0 1.0"})
  void conversion(String property) {
    BoundingBox boundingBox = BoundingBoxUtil.toBoundingBox(property);
    String string = BoundingBoxUtil.toString(boundingBox);
    String[] parts = property.split(" ");
    assertEquals(string, property);
    assertEquals(boundingBox.getMinLatitude(), Double.parseDouble(parts[0]));
    assertEquals(boundingBox.getMaxLatitude(), Double.parseDouble(parts[1]));
    assertEquals(boundingBox.getMinLongitude(), Double.parseDouble(parts[2]));
    assertEquals(boundingBox.getMaxLongitude(), Double.parseDouble(parts[3]));
  }

  // isWorld : boundingBox
  @ParameterizedTest(name = "isWorld : {0} -> {1}")
  @CsvSource({
          "-90 90 -180 180, true",
          "-90.0 90.0 -180.0 180.0, true",
          "-90.00 90.00 -180.00 180.00, true",
          "-89.99999 90.00 -180.00 180.00, false",
          "0 0 0 0, false",
          "-1 1 -1 1, false",
          "-89.99 90    -180    180, false",
          "-90    89.99 -180    180, false",
          "-90    90    -179.99 180, false",
          "-90    90    -180    179.00, false"
  })
  void isWorld(String boundingBox, boolean expected) {
    assertEquals(expected, BoundingBoxUtil.isWorld(BoundingBoxUtil.toBoundingBox(boundingBox)));
  }
}
