package com.bpm.measurementqueryservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomDataDTOTest {

    @Test
    void shouldCreateRoomDataDTOWithAllFields() {
        RoomDataDTO roomDataDTO = RoomDataDTO.builder()
                .location("Kitchen")
                .currentTemperature(22.5)
                .currentHumidity(45.0)
                .sensorCount(3)
                .build();

        assertEquals("Kitchen", roomDataDTO.location());
        assertEquals(22.5, roomDataDTO.currentTemperature());
        assertEquals(45.0, roomDataDTO.currentHumidity());
        assertEquals(3, roomDataDTO.sensorCount());
    }

    @Test
    void shouldHandleZeroSensorCount() {
        RoomDataDTO roomDataDTO = RoomDataDTO.builder()
                .location("Basement")
                .currentTemperature(18.0)
                .currentHumidity(60.0)
                .sensorCount(0)
                .build();

        assertEquals(0, roomDataDTO.sensorCount());
    }

    @Test
    void shouldBeEqualWhenFieldsAreTheSame() {
        RoomDataDTO dto1 = RoomDataDTO.builder()
                .location("Kitchen")
                .currentTemperature(22.5)
                .currentHumidity(45.0)
                .sensorCount(3)
                .build();

        RoomDataDTO dto2 = RoomDataDTO.builder()
                .location("Kitchen")
                .currentTemperature(22.5)
                .currentHumidity(45.0)
                .sensorCount(3)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
