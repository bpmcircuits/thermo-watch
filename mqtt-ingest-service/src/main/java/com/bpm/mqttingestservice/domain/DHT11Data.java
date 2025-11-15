package com.bpm.mqttingestservice.domain;

import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DHT11Data implements SensorData {
    @JsonProperty("Temperature")
    private double temperature;

    @JsonProperty("Humidity")
    private double humidity;

    @JsonProperty("DewPoint")
    private double dewPoint;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    @Override
    public SensorMeasurementEvent toMeasurementEvent(String topic) {
        return SensorMeasurementEvent.builder()
                .sensorName(topic)
                .sensorId(null)
                .temperature(this.getTemperature())
                .humidity(this.getHumidity())
                .dewPoint(this.getDewPoint())
                .timestamp(LocalDateTime.now())
                .build();
    }
}