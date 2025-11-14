package com.bpm.mqttingestservice.rabbit.dto;

import lombok.Builder;

@Builder
public record SensorMeasurementEvent(
        String sensorName,
        String sensorId,
        String location,
        double temperature,
        double humidity,
        double dewPoint
) {
}
