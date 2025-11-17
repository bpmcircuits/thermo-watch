package com.bpm.mqttingestservice.rabbit.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SensorMeasurementEvent(
        String sensorType,
        String sensorId,
        String location,
        Double temperature,
        Double humidity,
        Double dewPoint,
        LocalDateTime timestamp
) {
}
