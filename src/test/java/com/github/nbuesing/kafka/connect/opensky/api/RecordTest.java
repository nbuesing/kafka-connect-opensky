package com.github.nbuesing.kafka.connect.opensky.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

  private static final Object[] GOOD = new Object[]{
          "icao24",
          "callSign",
          "originCountry",
          0L, // timePosition
          0L, // lastContact
          0.0, // longitude
          0.0, // latitude
          0.0, // baroAltitude
          false, // onGroup
          0.0, //velocity
          0.0, //trueTrack
          0.0, //verticalRate
          null, //sensors
          0.0, //geoAltitude
          0.0, //squawk
          false, // spi
          0 //positionSource
  };

  @Test
  void good() {
    Assertions.assertDoesNotThrow(() -> new Record(GOOD));
  }

  @Test
  void bad() {
    Assertions.assertThrows(RuntimeException.class, () -> new Record(new Object[]{"icao24"}));
  }

  @Test
  void nullString() {
    GOOD[0] = null;
    Record record = new Record(GOOD);
    Assertions.assertNull(record.getIcao24());
  }

  @Test
  void booleanString() {
    GOOD[0] = Boolean.FALSE;
    Record record = new Record(GOOD);
    Assertions.assertEquals("false", record.getIcao24());
  }

  @Test
  void nullLong() {
    GOOD[3] = null;
    Record record = new Record(GOOD);
    Assertions.assertNull(record.getTimePosition());
  }

  @Test
  void doubleLong() {
    GOOD[3] = 3.3;
    Record record = new Record(GOOD);
    Assertions.assertEquals(3, record.getTimePosition());
  }

  @Test
  void invalidLong() {
    GOOD[3] = "10";
    Record record = new Record(GOOD);
    Assertions.assertEquals(null, record.getTimePosition());
  }

  @Test
  void nullDouble() {
    GOOD[5] = null;
    Record record = new Record(GOOD);
    Assertions.assertNull(record.getLongitude());
  }

  @Test
  void integerDouble() {
    GOOD[5] = 3;
    Record record = new Record(GOOD);
    Assertions.assertEquals(3.0, record.getLongitude());
  }

  @Test
  void invalidDouble() {
    GOOD[5] = "3.0";
    Record record = new Record(GOOD);
    Assertions.assertEquals(null, record.getLongitude());
  }

  @Test
  void nullInteger() {
    GOOD[16] = null;
    Record record = new Record(GOOD);
    Assertions.assertNull(record.getPositionSource());
  }

  @Test
  void doubleInteger() {
    GOOD[16] = 5.6;
    Record record = new Record(GOOD);
    Assertions.assertEquals(5, record.getPositionSource());
  }

  @Test
  void invalidInteger() {
    GOOD[16] = "5";
    Record record = new Record(GOOD);
    Assertions.assertEquals(null, record.getPositionSource());
  }


  @Test
  void nullBoolean() {
    GOOD[8] = null;
    Record record = new Record(GOOD);
    Assertions.assertNull(record.getOnGround());
  }

  @Test
  void invalidBoolean() {
    GOOD[8] = "FALSE";
    Record record = new Record(GOOD);
    Assertions.assertEquals(null, record.getOnGround());
  }

}