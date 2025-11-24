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
    void shouldIgnoreUnknownProperties() throws Exception {
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

    @Test
    void shouldConvertToMeasurementEvent() throws Exception {
        // Given
        DS18B20Data data = createDS18B20Data("28-00000123abcd", 25.0);
        String topic = "temp_bathroom";

        // When
        SensorMeasurementEvent event = data.toMeasurementEvent(topic);

        // Then
        assertEquals("DS18B20", event.sensorType());
        assertEquals("28-00000123abcd", event.sensorId());
        assertEquals(topic, event.location());
        assertEquals(new BigDecimal("25"), event.temperature());
        assertNull(event.humidity());
        assertNull(event.dewPoint());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldUseSensorIdFromJsonData() throws Exception {
        // Given
        DS18B20Data data1 = createDS18B20Data("28-sensor-001", 25.0);
        DS18B20Data data2 = createDS18B20Data("28-sensor-002", 26.0);
        String topic = "temp_bathroom";

        // When
        String sensorId1 = data1.toMeasurementEvent(topic).sensorId();
        String sensorId2 = data2.toMeasurementEvent(topic).sensorId();

        // Then
        assertNotEquals(sensorId1, sensorId2);
        assertEquals("28-sensor-001", sensorId1);
        assertEquals("28-sensor-002", sensorId2);
    }

    @Test
    void shouldMaintainSensorIdAcrossDifferentTopics() throws Exception {
        // Given
        DS18B20Data data = createDS18B20Data("28-sensor-001", 25.0);
        String topic1 = "temp_bathroom";
        String topic2 = "temp_livingroom";

        // When
        String sensorId1 = data.toMeasurementEvent(topic1).sensorId();
        String sensorId2 = data.toMeasurementEvent(topic2).sensorId();

        // Then
        assertEquals(sensorId1, sensorId2);
        assertEquals("28-sensor-001", sensorId1);
    }

    private DS18B20Data createDS18B20Data(String id, double temperature) throws Exception {
        String json = String.format("""
                {
                    "Id": "%s",
                    "Temperature": %f
                }
                """, id, temperature);

        return objectMapper.readValue(json, DS18B20Data.class);
    }
}
