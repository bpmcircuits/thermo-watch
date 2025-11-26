package com.bpm.mqttingestservice.domain;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DS18B20DataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        // Given
        String json = """
                {
                    "Id": "28-00000123abcd",
                    "Temperature": 23.5
                }
                """;

        // When
        DS18B20Data data = objectMapper.readValue(json, DS18B20Data.class);

        // Then
        assertEquals("28-00000123abcd", data.getId());
        assertEquals(23.5, data.getTemperature());
    }

    @Test
    void shouldIgnoreUnknownProperties() {
        // Given
        String json = """
                {
                    "Id": "28-00000123abcd",
                    "Temperature": 23.5,
                    "UnknownField": "value"
                }
                """;

        // When & Then
        assertDoesNotThrow(() -> objectMapper.readValue(json, DS18B20Data.class));
    }
}
