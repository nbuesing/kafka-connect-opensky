package com.github.nbuesing.kafka.connect.opensky.api

import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

class RecordSpec extends Specification {

    private static final Random RANDOM = new Random();
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

    def 'invalid'() {
        setup:
        String[] values = [null] * 16

        when:
        new Record(values)

        then:
        thrown(RuntimeException)
    }

    def 'null check'() {

        setup:
        String[] allNulls = [null] * 17

        when:
        Record record = new Record(allNulls)

        then:
        record.getProperties().each { k, v ->
            if (k != 'class') {
                assert v == null
            }
        }
    }

    def 'invalid check'() {

        setup:
        String[] invalidData = ["ONLY_STRINGS"] * 17

        when:
        Record record = new Record(invalidData)

        then:
        record.getProperties().each { k, v ->
            if (k == 'class') {
                assert v != null
            } else if (v instanceof String) {
                assert v != null
            } else {
                assert v == null
            }
        }
    }


    def 'random check'() {

        setup:
        Object[] random = [
                RandomStringUtils.randomAlphabetic(8),
                RandomStringUtils.randomAlphabetic(8),
                RandomStringUtils.randomAlphabetic(8),
                RANDOM.nextLong(),
                RANDOM.nextLong(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextBoolean(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                null, // sensors which we ignore
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextBoolean(),
                RANDOM.nextInt()
        ]

        when:
        Record record = new Record(random)

        then:
        record.getProperties().each { k, v ->
            assert v != null
        }
        int i = 0
        assert random[i++] == record.icao24
        assert random[i++] == record.callSign
        assert random[i++] == record.originCountry
        assert random[i++] == record.timePosition
        assert random[i++] == record.lastContact
        assert random[i++] == record.longitude
        assert random[i++] == record.latitude
        assert random[i++] == record.baroAltitude
        assert random[i++] == record.onGround
        assert random[i++] == record.velocity
        assert random[i++] == record.trueTrack
        assert random[i++] == record.verticalRate
        i++ // sensors
        assert random[i++] == record.geoAltitude
        assert random[i++] == record.squawk
        assert random[i++] == record.spi
        assert random[i] == record.positionSource
    }

    def 'random check -- datatype conversion'() {

        setup:
        Object[] random = [
                new StringBuilder(RandomStringUtils.randomAlphabetic(8)),
                new StringBuilder(RandomStringUtils.randomAlphabetic(8)),
                new StringBuilder(RandomStringUtils.randomAlphabetic(8)),
                RANDOM.nextLong() as Double,
                RANDOM.nextLong() as Double,
                RANDOM.nextDouble() as BigDecimal,
                RANDOM.nextFloat() as Float,
                RANDOM.nextDouble(),
                RANDOM.nextBoolean(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                null, // sensors which we ignore
                RANDOM.nextDouble(),
                RANDOM.nextDouble(),
                RANDOM.nextBoolean(),
                RANDOM.nextLong()
        ]

        when:
        Record record = new Record(random)

        then:
        record.getProperties().each { k, v ->
            assert v != null
        }
    }

}
