package com.bpm.measurementqueryservice.dto;

import lombok.Builder;

@Builder
public record SensorDTO(
        Long id,
        String sensorId,
        String location,
        String sensorType,
        String lastSeen,
        Boolean isOnline
) {
}
