package com.github.nbuesing.kafka.connect.opensky.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Records {
    private Integer time;
    private List<Record> states;
}
