package com.bpm.mqttingestservice.rabbit.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SensorAvailabilityEvent(
        String sensorLocation,
        String status,
        String source,
        LocalDateTime timestamp
) {
}
