package com.bpm.measurementqueryservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementDTOTest {

    @Test
    void shouldCreateMeasurementDTOWithAllFields() {
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .id("1")
                .sensorId("TEST-001")
                .temperature(22.5)
                .humidity(45.0)
                .timestamp("2024-01-01T12:00:00")
                .build();

        assertEquals("1", measurementDTO.id());
        assertEquals("TEST-001", measurementDTO.sensorId());
        assertEquals(22.5, measurementDTO.temperature());
        assertEquals(45.0, measurementDTO.humidity());
        assertEquals("2024-01-01T12:00:00", measurementDTO.timestamp());
    }

    @Test
    void shouldHandleNegativeTemperature() {
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .id("2")
                .sensorId("TEST-002")
                .temperature(-5.5)
                .humidity(80.0)
                .timestamp("2024-01-01T08:00:00")
                .build();

        assertEquals(-5.5, measurementDTO.temperature());
    }

    @Test
    void shouldBeEqualWhenFieldsAreTheSame() {
        MeasurementDTO dto1 = MeasurementDTO.builder()
                .id("1")
                .sensorId("TEST-001")
                .temperature(22.5)
                .humidity(45.0)
                .timestamp("2024-01-01T12:00:00")
                .build();

        MeasurementDTO dto2 = MeasurementDTO.builder()
                .id("1")
                .sensorId("TEST-001")
                .temperature(22.5)
                .humidity(45.0)
                .timestamp("2024-01-01T12:00:00")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
