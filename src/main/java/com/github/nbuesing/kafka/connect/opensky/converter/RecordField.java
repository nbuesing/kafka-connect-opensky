package com.github.nbuesing.kafka.connect.opensky.converter;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Timestamp;


public enum RecordField {

    IACO24("id", SchemaBuilder.string().doc("Unique ICAO 24-bit address of the transponder in hex string representation.").build()),
    CALLSIGN("callsign", SchemaBuilder.string().optional().doc("Callsign of the vehicle (8 chars). Can be null if no call-sign has been received.").build()),
    ORIGIN_COUNTRY("originCountry", SchemaBuilder.string().optional().doc("Country name inferred from the ICAO 24-bit address.").build()),
    LAST_POSITION_UPDATE("timePosition", Timestamp.builder().optional().doc("Unix timestamp (seconds) for the last position update. Can be null if no position report was received by OpenSky within the past 15s.").build()),
    LAST_CONTACT("lastContact", Timestamp.builder().optional().doc("Unix timestamp (seconds) for the last update in general. This field is updated for any new, valid message received from the transponder.").build()),
    LONGITUDE("lon", SchemaBuilder.float64().doc("geographical location in WSG-84 decimal degrees.").build()),
    LATITUDE("lat", SchemaBuilder.float64().doc("WGS-84 longitude in decimal degrees.").build()),
    BARO_ALTITUDE("barometricAltitude", SchemaBuilder.float64().optional().doc("Barometric altitude in meters. Can be null.").build()),
    ON_GROUND("onGround", SchemaBuilder.bool().optional().doc("Boolean value which indicates if the position was retrieved from a surface position report.").build()),
    VELOCITY("velocity", SchemaBuilder.float64().optional().doc("Velocity over ground in m/s. Can be null.").build()),
    HEADING("heading", SchemaBuilder.float64().optional().doc("True track in decimal degrees clockwise from north (north=0°). Can be null.").build()),
    VERTICAL_RATE("verticalRate", SchemaBuilder.float64().optional().doc("Vertical rate in m/s. A positive value indicates that the airplane is climbing, a negative value indicates that it descends. Can be null.").build()),
    GEO_ALTITUDE("geometricAltitude", SchemaBuilder.float64().optional().doc("Geometric altitude in meters. Can be null.").build()),
    SQUAWK("squawk", SchemaBuilder.string().optional().doc("The transponder code aka Squawk. Can be null.").build()),
    SPI("specialPurpose", SchemaBuilder.bool().optional().doc("Whether flight status indicates special purpose indicator.").build()),
    POSITION_SOURCE("positionSource", SchemaBuilder.string().optional().doc("Origin of this state’s position: ADS-B, ASTERIX, MLAT, FLAM, UNKNOWN").build());

    private String field;
    private Schema schema;

    RecordField(final String field, final Schema schema) {
        this.field = field;
        this.schema = schema;
    }

    public String field() {
        return this.field;
    }

    public Schema schema() {
        return this.schema;
    }

}
