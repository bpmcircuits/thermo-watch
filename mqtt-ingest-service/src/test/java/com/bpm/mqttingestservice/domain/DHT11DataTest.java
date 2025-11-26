package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DHT11DataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        // Given
        String json = """
                {
                    "Temperature": 23.5,
                    "Humidity": 60.0,
                    "DewPoint": 15.2
                }
                """;

        // When
        DHT11Data data = objectMapper.readValue(json, DHT11Data.class);

        // Then
        assertEquals(23.5, data.getTemperature());
        assertEquals(60.0, data.getHumidity());
        assertEquals(15.2, data.getDewPoint());
    }

    @Test
    void shouldIgnoreUnknownProperties() {
        // Given
        String json = """
                {
                    "Temperature": 23.5,
                    "Humidity": 60.0,
                    "DewPoint": 15.2,
                    "UnknownField": "value"
                }
                """;

        // When & Then
        assertDoesNotThrow(() -> objectMapper.readValue(json, DHT11Data.class));
    }
}
