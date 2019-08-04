package com.github.nbuesing.kafka.connect.opensky.converter;


import com.github.nbuesing.kafka.connect.opensky.api.Record;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.Date;

import static com.github.nbuesing.kafka.connect.opensky.converter.RecordField.*;

public final class RecordConverter {

    private RecordConverter() {
    }

    public static final Schema SCHEMA_KEY = Schema.STRING_SCHEMA;

    public static final Schema GEO_LOCATION_SCHEMA = SchemaBuilder.struct()
            .name("com.github.nbuesing.kafka.kafka.connect.opensky.GeoLocation")
            .version(1)
            .optional()
            .doc("geolocation position")
            .field(LATITUDE.field(), LATITUDE.schema())
            .field(LONGITUDE.field(), LONGITUDE.schema())
            .build();

    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("com.github.nbuesing.kafka.kafka.connect.opensky.OpenSky")
            .version(1)
            .field(IACO24.field(), IACO24.schema())
            .field(CALLSIGN.field(), CALLSIGN.schema())
            .field(ORIGIN_COUNTRY.field(), ORIGIN_COUNTRY.schema())
            .field(LAST_POSITION_UPDATE.field(), LAST_POSITION_UPDATE.schema())
            .field(LAST_CONTACT.field(), LAST_CONTACT.schema())
            .field("location", GEO_LOCATION_SCHEMA)
            .field(BARO_ALTITUDE.field(), BARO_ALTITUDE.schema())
            .field(ON_GROUND.field(), ON_GROUND.schema())
            .field(VELOCITY.field(), VELOCITY.schema())
            .field(HEADING.field(), HEADING.schema())
            .field(VERTICAL_RATE.field(), VERTICAL_RATE.schema())
            //todo sensors int[]
            .field(GEO_ALTITUDE.field(), GEO_ALTITUDE.schema())
            .field(SQUAWK.field(), SQUAWK.schema())
            .field(SPI.field(), SPI.schema())
            .field(POSITION_SOURCE.field(), POSITION_SOURCE.schema())
            .build();


    public static Struct convert(final Record record) {

        final Struct struct = new Struct(SCHEMA);

        struct.put(IACO24.field(), record.getIcao24());
        struct.put(CALLSIGN.field(), record.getCallSign());
        struct.put(ORIGIN_COUNTRY.field(), record.getOriginCountry());
        struct.put(LAST_POSITION_UPDATE.field(), fromEpoc(record.getTimePosition()));
        struct.put(LAST_CONTACT.field(), fromEpoc(record.getLastContact()));
        struct.put(ORIGIN_COUNTRY.field(), record.getOriginCountry());
        struct.put("location",
                new Struct(GEO_LOCATION_SCHEMA)
                        .put(LATITUDE.field(), record.getLatitude())
                        .put(LONGITUDE.field(), record.getLongitude())
        );
        struct.put(BARO_ALTITUDE.field(), record.getBaroAltitude());
        struct.put(ON_GROUND.field(), record.getOnGround());
        struct.put(VELOCITY.field(), record.getVelocity());
        struct.put(HEADING.field(), record.getTrueTrack());
        struct.put(VERTICAL_RATE.field(), record.getVerticalRate());
        struct.put(GEO_ALTITUDE.field(), record.getGeoAltitude());
        struct.put(SQUAWK.field(), record.getSquawk());
        struct.put(SPI.field(), record.getSpi());
        struct.put(POSITION_SOURCE.field(), record.getPositionSource() != null ? "" + record.getPositionSource() : null);

        return struct;
    }

    /**
     * Convert the OpenSky's Epoc (seconds since 1970 in a double)
     */
    private static Date fromEpoc(final Long value) {
        return (value != null) ? new Date(value * 1000L) : null;
    }
}