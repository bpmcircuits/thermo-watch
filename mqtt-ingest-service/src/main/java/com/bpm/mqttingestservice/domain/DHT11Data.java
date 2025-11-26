package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DHT11Data implements SensorData {

    private static final String SENSOR_TYPE = "DHT11";

    @JsonProperty("Temperature")
    private double temperature;

    @JsonProperty("Humidity")
    private double humidity;

    @JsonProperty("DewPoint")
    private double dewPoint;

    public static String sensorType() {
        return SENSOR_TYPE;
    }

}