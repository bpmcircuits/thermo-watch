package com.bpm.measurementstorageservice.rabbit.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SensorMeasurementEvent(
        String sensorType,
        String sensorId,
        String location,
        BigDecimal temperature,
        BigDecimal humidity,
        BigDecimal dewPoint,
        LocalDateTime timestamp
) {
}
