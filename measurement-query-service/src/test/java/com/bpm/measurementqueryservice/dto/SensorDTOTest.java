package com.bpm.measurementqueryservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorDTOTest {

    @Test
    void shouldCreateSensorDTOWithAllFields() {
        SensorDTO sensorDTO = SensorDTO.builder()
                .id(1L)
                .sensorId("TEST-001")
                .location("Kitchen")
                .sensorType("DHT22")
                .lastSeen("2024-01-01T12:00:00")
                .isOnline(true)
                .build();

        assertEquals(1L, sensorDTO.id());
        assertEquals("TEST-001", sensorDTO.sensorId());
        assertEquals("Kitchen", sensorDTO.location());
        assertEquals("DHT22", sensorDTO.sensorType());
        assertEquals("2024-01-01T12:00:00", sensorDTO.lastSeen());
        assertTrue(sensorDTO.isOnline());
    }

    @Test
    void shouldCreateOfflineSensorDTO() {
        SensorDTO sensorDTO = SensorDTO.builder()
                .id(2L)
                .sensorId("TEST-002")
                .location("Bedroom")
                .sensorType("DHT11")
                .lastSeen("2024-01-01T10:00:00")
                .isOnline(false)
                .build();

        assertFalse(sensorDTO.isOnline());
    }

    @Test
    void shouldBeEqualWhenFieldsAreTheSame() {
        SensorDTO dto1 = SensorDTO.builder()
                .id(1L)
                .sensorId("TEST-001")
                .location("Kitchen")
                .sensorType("DHT22")
                .lastSeen("2024-01-01T12:00:00")
                .isOnline(true)
                .build();

        SensorDTO dto2 = SensorDTO.builder()
                .id(1L)
                .sensorId("TEST-001")
                .location("Kitchen")
                .sensorType("DHT22")
                .lastSeen("2024-01-01T12:00:00")
                .isOnline(true)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
