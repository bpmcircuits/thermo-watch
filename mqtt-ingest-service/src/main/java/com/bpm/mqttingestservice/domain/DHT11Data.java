package com.bpm.mqttingestservice.domain;

import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

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

        String idFromTopic = UUID.nameUUIDFromBytes(topic.getBytes(StandardCharsets.UTF_8)).toString();

        return SensorMeasurementEvent.builder()
                .sensorType(SENSOR_TYPE)
                .sensorId(idFromTopic)
                .location(topic)
                .temperature(this.getTemperature())
                .humidity(this.getHumidity())
                .dewPoint(this.getDewPoint())
                .timestamp(LocalDateTime.now())
                .build();
    }
}