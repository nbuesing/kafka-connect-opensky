package com.github.nbuesing.kafka.connect.opensky.api;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BoundingBox {
    private final Double minLatitude;
    private final Double maxLatitude;
    private final Double minLongitude;
    private final Double maxLongitude;
}
