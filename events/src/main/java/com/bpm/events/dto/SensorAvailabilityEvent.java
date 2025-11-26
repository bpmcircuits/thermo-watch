package com.bpm.events.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SensorAvailabilityEvent(
        String sensorId,
        String status,
        String source,
        LocalDateTime timestamp
) {
}
