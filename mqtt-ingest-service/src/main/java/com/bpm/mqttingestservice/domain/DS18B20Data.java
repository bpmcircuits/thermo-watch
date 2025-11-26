package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DS18B20Data implements SensorData {

    private static final String SENSOR_TYPE = "DS18B20";

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Temperature")
    private double temperature;

    public static String sensorType() {
        return SENSOR_TYPE;
    }

}
