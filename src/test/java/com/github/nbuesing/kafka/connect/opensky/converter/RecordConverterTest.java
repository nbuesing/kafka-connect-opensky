package com.github.nbuesing.kafka.connect.opensky.converter;

import com.github.nbuesing.kafka.connect.opensky.api.Record;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecordConverterTest {

  @Test
  void convert() {
    long now = System.currentTimeMillis() / 1000L;
    long nowMinus2 = now - 2L;

    Record stateVector = new Record();
    stateVector.setIcao24("ICAO24");
    stateVector.setCallSign("CALLSIGN");
    stateVector.setLatitude(81.23);
    stateVector.setLongitude(145.67);
    stateVector.setBaroAltitude(32.1);
    stateVector.setGeoAltitude(43.2);
    stateVector.setVelocity(54.3);
    stateVector.setTrueTrack(65.4);
    stateVector.setOnGround(true);
    stateVector.setOriginCountry("USA");
    stateVector.setTimePosition(now);
    stateVector.setLastContact(nowMinus2);
    stateVector.setSpi(true);
    stateVector.setPositionSource(1);

    Struct struct = RecordConverter.convert(stateVector);

    assertEquals("ICAO24", struct.get("id"));
    assertEquals("CALLSIGN", struct.get("callsign"));
    assertEquals(81.23, ((Struct) struct.get("location")).get("lat"));
    assertEquals(145.67, ((Struct) struct.get("location")).get("lon"));
    assertEquals("USA", struct.get("originCountry"));
    assertEquals(32.1, struct.get("barometricAltitude"));
    assertEquals(43.2, struct.get("geometricAltitude"));
    assertEquals(54.3, struct.get("velocity"));
    assertEquals(65.4, struct.get("heading"));
    assertEquals(true, struct.get("onGround"));
    assertEquals(true, struct.get("specialPurpose"));
    assertEquals("1", struct.get("positionSource"));
    assertEquals(new java.util.Date(now * 1000L), struct.get("timePosition"));
    assertEquals(new java.util.Date(nowMinus2 * 1000L), struct.get("lastContact"));
  }

  @Test
  void convert_nulls() {
    Record stateVector = new Record();
    stateVector.setIcao24("ICAO24");
    stateVector.setLatitude(12.34);
    stateVector.setLongitude(167.89);

    Struct struct = RecordConverter.convert(stateVector);

    assertEquals("ICAO24", struct.get("id"));
    assertNull(struct.get("callsign"));
    assertEquals(12.34, ((Struct) struct.get("location")).get("lat"));
    assertEquals(167.89, ((Struct) struct.get("location")).get("lon"));
    assertNull(struct.get("originCountry"));
    assertNull(struct.get("barometricAltitude"));
    assertNull(struct.get("geometricAltitude"));
    assertNull(struct.get("velocity"));
    assertNull(struct.get("heading"));
    assertNull(struct.get("onGround"));
    assertNull(struct.get("specialPurpose"));
    assertNull(struct.get("positionSource"));
    assertNull(struct.get("timePosition"));
    assertNull(struct.get("lastContact"));
  }

}
