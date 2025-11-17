package com.bpm.measurementqueryservice.dto;

public record RoomDataDTO(
        String location,
        Double currentTemperature,
        Double currentHumidity,
        Integer sensorCount
) {
}
