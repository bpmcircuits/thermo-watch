package com.bpm.measurementstorageservice.rabbit.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SensorMeasurementEvent(
        String sensorName,
        String sensorId,
        String location,
        Double temperature,
        Double humidity,
        Double dewPoint,
        LocalDateTime timestamp
) {
}
