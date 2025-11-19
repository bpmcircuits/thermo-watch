package com.bpm.measurementstorageservice.rabbit.dto;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
public record SensorAvailabilityEvent(
        String sensorLocation,
        String status,
        String source,
        LocalDateTime timestamp
) {
}
