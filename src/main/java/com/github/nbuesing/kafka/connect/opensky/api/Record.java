package com.github.nbuesing.kafka.connect.opensky.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Index    Property	    Type	Description
 * -------- --------------- ------- -----------
 * 0	    icao24	        string	Unique ICAO 24-bit address of the transponder in hex string representation.
 * 1	    callsign	    string	Callsign of the vehicle (8 chars). Can be null if no callsign has been received.
 * 2	    origin_country	string	Country name inferred from the ICAO 24-bit address.
 * 3	    time_position	int	    Unix timestamp (seconds) for the last position update. Can be null if no position report was received by OpenSky within the past 15s.
 * 4	    last_contact    int	    Unix timestamp (seconds) for the last update in general. This field is updated for any new, valid message received from the transponder.
 * 5	    longitude	    float	WGS-84 longitude in decimal degrees. Can be null.
 * 6	    latitude	    float	WGS-84 latitude in decimal degrees. Can be null.
 * 7	    baro_altitude	float	Barometric altitude in meters. Can be null.
 * 8	    on_ground	    boolean	Boolean value which indicates if the position was retrieved from a surface position report.
 * 9	    velocity	    float	Velocity over ground in m/s. Can be null.
 * 10	    true_track	    float	True track in decimal degrees clockwise from north (north=0°). Can be null.
 * 11	    vertical_rate	float	Vertical rate in m/s. A positive value indicates that the airplane is climbing, a negative value indicates that it descends. Can be null.
 * 12	    sensors	        int[]	IDs of the receivers which contributed to this state vector. Is null if no filtering for sensor was used in the request.
 * 13	    geo_altitude	float	Geometric altitude in meters. Can be null.
 * 14	    squawk	        string	The transponder code aka Squawk. Can be null.
 * 15	    spi	            boolean	Whether flight status indicates special purpose indicator.
 * 16	    position_source int	    Origin of this state’s position: 0 = ADS-B, 1 = ASTERIX, 2 = MLAT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Record {

    private String icao24;
    private String callSign;
    private String originCountry;
    private Long timePosition;
    private Long lastContact;
    private Double longitude;
    private Double latitude;
    private Double baroAltitude;
    private Boolean onGround;
    private Double velocity;
    private Double trueTrack;
    private Double verticalRate;
    private Double geoAltitude;
    private Double squawk;
    private Boolean spi;
    private Integer positionSource;

    @JsonCreator
    public Record(Object[] data) {

        if (data.length < 17) {
            throw new RuntimeException("unexpected record size.");
        }

        icao24 = toStringValue(data[0]);
        callSign = toStringValue(data[1]);
        originCountry = toStringValue(data[2]);
        timePosition = toLongValue(data[3]);
        lastContact = toLongValue(data[4]);
        longitude = toDoubleValue(data[5]);
        latitude = toDoubleValue(data[6]);
        baroAltitude = toDoubleValue(data[7]);
        onGround = toBooleanValue(data[8]);
        velocity = toDoubleValue(data[9]);
        trueTrack = toDoubleValue(data[10]);
        verticalRate = toDoubleValue(data[11]);
        // ignoring 'sensors' attribute as it is always null for all queries
        geoAltitude = toDoubleValue(data[13]);
        squawk = toDoubleValue(data[14]);
        spi = toBooleanValue(data[15]);
        positionSource = toIntegerValue(data[16]);
    }

    private String toStringValue(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return ((String) object).trim();
        } else {
            return object.toString().trim();
        }
    }

    private Double toDoubleValue(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Double) {
            return ((Double) object);
        } else if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else {
            // unexpected data
            return null;
        }
    }

    private Integer toIntegerValue(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Integer) {
            return ((Integer) object);
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        } else {
            // unexpected data
            return null;
        }
    }

    private Long toLongValue(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Long) {
            return ((Long) object);
        } else if (object instanceof Number) {
            return ((Number) object).longValue();
        } else {
            // unexpected data
            return null;
        }
    }

    private Boolean toBooleanValue(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Boolean) {
            return ((Boolean) object);
        } else {
            // unexpected data
            return null;
        }
    }

}
