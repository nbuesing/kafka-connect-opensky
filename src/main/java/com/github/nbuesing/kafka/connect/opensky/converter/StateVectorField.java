package com.github.nbuesing.kafka.connect.opensky.converter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StateVectorField {

    public static final StateVectorField IACO24 = new StateVectorField(
            "icao24",
            0,
            "icao24",
            "string",
            "Unique ICAO 24-bit address of the transponder in hex string representation."
    );

    public static final StateVectorField CALL_SIGN = new StateVectorField(
            "callSign",
            1,
            "call_sign",
            "string",
            "Callsign of the vehicle (8 chars). Can be null if no callsign has been received."
    );

    public static final StateVectorField ORIGIN_COUNTRY = new StateVectorField(
            "originCountry",
            2,
            "origin_country",
            "string",
            "Country name inferred from the ICAO 24-bit address."
    );

    public static final StateVectorField LAST_POSITION_UPDATE = new StateVectorField(
            "timePosition",
            3,
            "time_position",
            "int",
            "Unix timestamp (seconds) for the last position update. Can be null if no position report was received by OpenSky within the past 15s."
    );

    public static final StateVectorField LAST_CONTACT = new StateVectorField(
            "lastContact",
            4,
            "last_contact",
            "int",
            "Unix timestamp (seconds) for the last update in general. This field is updated for any new, valid message received from the transponder."
    );

    public static final StateVectorField GEO_LOCATION = new StateVectorField(
            "location",
            -1,
            null,
            "struct",
            "geographical location in WSG-84 decimal degrees."
    );

    public static final StateVectorField LONGITUDE = new StateVectorField(
            "longitude",
            5,
            "longitude",
            "float",
            "WGS-84 longitude in decimal degrees."
    );

    public static final StateVectorField LATITUDE = new StateVectorField(
            "latitude",
            6,
            "latitude",
            "float",
            "WGS-84 latitude in decimal degrees."
    );

    public static final StateVectorField BARO_ALTITUDE = new StateVectorField(
            "barometricAltitude",
            7,
            "baro_altitude",
            "float",
            "Barometric altitude in meters. Can be null."
    );

    public static final StateVectorField ON_GROUND = new StateVectorField(
            "onGround",
            8,
            "on_ground",
            "boolean",
            "Boolean value which indicates if the position was retrieved from a surface position report."
    );

    public static final StateVectorField VELOCITY = new StateVectorField(
            "velocity",
            9,
            "velocity",
            "float",
            "Velocity over ground in m/s. Can be null."
    );

    public static final StateVectorField HEADING = new StateVectorField(
            "heading",
            10,
            "true_track",
            "float",
            "True track in decimal degrees clockwise from north (north=0°). Can be null."
    );

    public static final StateVectorField VERTICAL_RATE = new StateVectorField(
            "verticalRate",
            11,
            "vertical_rate",
            "float",
            "Vertical rate in m/s. A positive value indicates that the airplane is climbing, a negative value indicates that it descends. Can be null."
    );

    public static final StateVectorField SENSORS = new StateVectorField(
            "sensors",
            12,
            "sensors",
            "int[]",
            "IDs of the receivers which contributed to this state vector. Is null if no filtering for sensor was used in the request."
    );

    public static final StateVectorField GEO_ALTITUDE = new StateVectorField(
            "geometricAltitude",
            13,
            "geo_altitude",
            "float",
            "Geometric altitude in meters. Can be null."
    );

    public static final StateVectorField SQUAWK = new StateVectorField(
            "squawk",
            14,
            "squawk",
            "string",
            "The transponder code aka Squawk. Can be null."
    );

    public static final StateVectorField SPI = new StateVectorField(
            "specialPurpose",
            15,
            "spi",
            "boolean",
            "Whether flight status indicates special purpose indicator."
    );

    // removing enum as adding a new enum value breaks avro, so storing name of enum as a string.
    public static final StateVectorField POSITION_SOURCE = new StateVectorField(
            "positionSource",
            16,
            "position_source",
            "string",
            "Origin of this state’s position: ADS-B, ASTERIX, MLAT, FLAM, UNKNOWN"
    );

    private String field;
    private int position;
    private String openskyField;
    private String type;
    private String doc;

}
