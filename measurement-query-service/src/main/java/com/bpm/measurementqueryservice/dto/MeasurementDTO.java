package com.bpm.measurementqueryservice.dto;

public record MeasurementDTO(
        String id,
        String sensorId,
        Double temperature,
        Double humidity,
        String timestamp
) {
}
