package com.bpm.measurementqueryservice.dto;

import lombok.Builder;

@Builder
public record MeasurementDTO(
        String id,
        String sensorId,
        Double temperature,
        Double humidity,
        Double dewPoint,
        Double pressure,
        String timestamp
) {
}
