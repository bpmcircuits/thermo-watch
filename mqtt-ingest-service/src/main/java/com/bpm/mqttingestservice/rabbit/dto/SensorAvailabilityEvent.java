package com.bpm.mqttingestservice.rabbit.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SensorAvailabilityEvent(
        String sensorId,
        String status,
        String source,
        Instant timestamp
) {
}
