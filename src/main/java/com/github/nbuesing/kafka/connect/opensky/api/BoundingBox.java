package com.github.nbuesing.kafka.connect.opensky.api;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class BoundingBox {
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;
}
