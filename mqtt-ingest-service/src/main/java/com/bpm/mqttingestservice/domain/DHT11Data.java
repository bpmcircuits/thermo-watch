package com.bpm.mqttingestservice.domain;

import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DHT11Data implements SensorData {

    private static final String SENSOR_TYPE = "DHT11";

    @JsonProperty("Temperature")
    private double temperature;

    @JsonProperty("Humidity")
    private double humidity;

    @JsonProperty("DewPoint")
    private double dewPoint;

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    @Override
    public SensorMeasurementEvent toMeasurementEvent(String topic) {
        return SensorMeasurementEvent.builder()
                .sensorType(SENSOR_TYPE)
                .sensorId(null)
                .location(topic)
                .temperature(this.getTemperature())
                .humidity(this.getHumidity())
                .dewPoint(this.getDewPoint())
                .timestamp(LocalDateTime.now())
                .build();
    }
}