package com.bpm.measurementqueryservice.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Measurement {

    private String sensorType;
    private String sensorId;
    private Double temperature;
    private Double humidity;
    private String timestamp;
}
