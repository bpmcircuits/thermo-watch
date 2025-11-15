package com.bpm.mqttingestservice.domain;

import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DS18B20Data implements SensorData {
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

    @Override
    public SensorMeasurementEvent toMeasurementEvent(String topic) {
        return SensorMeasurementEvent.builder()
                .sensorName(topic)
                .sensorId(this.getId())
                .temperature(this.getTemperature())
                .humidity(null)
                .dewPoint(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
