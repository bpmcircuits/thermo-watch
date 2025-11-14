package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DS18B20Data {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Temperature")
    private double temperature;

    public String getId() {
        return id;
    }

    public double getTemperature() {
        return temperature;
    }
}
