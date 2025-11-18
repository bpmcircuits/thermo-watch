package com.bpm.measurementqueryservice.dto;

import lombok.Builder;

@Builder
public record RoomDataDTO(
        String location,
        Double currentTemperature,
        Double currentHumidity,
        Integer sensorCount
) {
}
