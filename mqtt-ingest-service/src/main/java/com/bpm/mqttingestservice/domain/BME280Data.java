package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BME280Data implements SensorData {
    private static final String SENSOR_TYPE = "BME280";

    @JsonProperty("Temperature")
    private double temperature;
    @JsonProperty("Humidity")
    private double humidity;
    @JsonProperty("DewPoint")
    private double dewPoint;
    @JsonProperty("Pressure")
    private double pressure;

    public static String sensorType() {
        return SENSOR_TYPE;
    }
}
