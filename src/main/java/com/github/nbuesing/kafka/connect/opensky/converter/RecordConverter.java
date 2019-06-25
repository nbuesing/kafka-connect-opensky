package com.github.nbuesing.kafka.connect.opensky.converter;


import com.github.nbuesing.kafka.connect.opensky.api.Record;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;

import java.util.Date;

import static com.github.nbuesing.kafka.connect.opensky.converter.StateVectorField.*;

public final class RecordConverter {

    private RecordConverter() {
    }

    public static final Schema SCHEMA_KEY = Schema.STRING_SCHEMA;

    public static final Schema GEO_LOCATION_SCHEMA = SchemaBuilder.struct()
            .name("com.github.nbuesing.kafka.kafka.connect.opensky.GeoLocation")
            .version(1)
            .optional()
            .doc("geolocation position")
            .field("latitude", SchemaBuilder.float64().doc("WGS-84 latitude in decimal degrees.").build())
            .field("longitude", SchemaBuilder.float64().doc("WGS-84 longitude in decimal degrees.").build())
            .build();

    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("com.github.nbuesing.kafka.kafka.connect.opensky.OpenSky")
            .version(1)
            .field(IACO24.getField(), SchemaBuilder.string().doc(IACO24.getDoc()).build())
            .field(CALL_SIGN.getField(), SchemaBuilder.string().optional().doc(CALL_SIGN.getDoc()).build())
            .field(ORIGIN_COUNTRY.getField(), SchemaBuilder.string().optional().doc(ORIGIN_COUNTRY.getDoc()).build())
            .field(LAST_POSITION_UPDATE.getField(), Timestamp.builder().optional().doc(LAST_POSITION_UPDATE.getDoc()).build())
            .field(LAST_CONTACT.getField(), Timestamp.builder().optional().doc(LAST_CONTACT.getDoc()).build())
            .field(GEO_LOCATION.getField(), GEO_LOCATION_SCHEMA)
            .field(BARO_ALTITUDE.getField(), SchemaBuilder.float64().optional().doc(BARO_ALTITUDE.getDoc()).build())
            .field(ON_GROUND.getField(), SchemaBuilder.bool().optional().doc(ON_GROUND.getDoc()).build())
            .field(VELOCITY.getField(), SchemaBuilder.float64().optional().doc(VELOCITY.getDoc()).build())
            .field(HEADING.getField(), SchemaBuilder.float64().optional().doc(HEADING.getDoc()).build())
            .field(VERTICAL_RATE.getField(), SchemaBuilder.float64().optional().doc(VERTICAL_RATE.getDoc()).build())
            //todo sensors int[]
            .field(GEO_ALTITUDE.getField(), SchemaBuilder.float64().optional().doc(GEO_ALTITUDE.getDoc()).build())
            .field(SQUAWK.getField(), SchemaBuilder.string().optional().doc(SQUAWK.getDoc()).build())
            .field(SPI.getField(), SchemaBuilder.bool().optional().doc(SPI.getDoc()).build())
            .field(POSITION_SOURCE.getField(), SchemaBuilder.string().optional().doc(POSITION_SOURCE.getDoc()).build())
            .build();


    public static Struct convert(final Record record) {

        final Struct struct = new Struct(SCHEMA);

        struct.put(IACO24.getField(), record.getIcao24());
        struct.put(CALL_SIGN.getField(), record.getCallSign());
        struct.put(ORIGIN_COUNTRY.getField(), record.getOriginCountry());
        struct.put(LAST_POSITION_UPDATE.getField(), fromEpoc(record.getTimePosition()));
        struct.put(LAST_CONTACT.getField(), fromEpoc(record.getLastContact()));
        struct.put(ORIGIN_COUNTRY.getField(), record.getOriginCountry());
        struct.put(GEO_LOCATION.getField(),
                new Struct(GEO_LOCATION_SCHEMA)
                        .put(LATITUDE.getField(), record.getLatitude())
                        .put(LONGITUDE.getField(), record.getLongitude())
        );
        struct.put(BARO_ALTITUDE.getField(), record.getBaroAltitude());
        struct.put(ON_GROUND.getField(), record.getOnGround());
        struct.put(VELOCITY.getField(), record.getVelocity());
        struct.put(HEADING.getField(), record.getTrueTrack());
        struct.put(VERTICAL_RATE.getField(), record.getVerticalRate());
        struct.put(GEO_ALTITUDE.getField(), record.getGeoAltitude());
        struct.put(SQUAWK.getField(), record.getSquawk());
        struct.put(SPI.getField(), record.getSpi());
        struct.put(POSITION_SOURCE.getField(), record.getPositionSource() != null ? "" + record.getPositionSource() : null);

        return struct;
    }

    /**
     * Convert the OpenSky's Epoc (seconds since 1970 in a double)
     */
    private static Date fromEpoc(final Long value) {
        return (value != null) ? new Date(value * 1000L) : null;
    }
}