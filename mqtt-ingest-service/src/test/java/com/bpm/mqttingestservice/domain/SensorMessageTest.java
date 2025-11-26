package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SensorMessageTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        // Given
        String json = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C",
                    "DHT11": {
                        "Temperature": 23.5,
                        "Humidity": 60.0
                    }
                }
                """;

        // When
        SensorMessage message = objectMapper.readValue(json, SensorMessage.class);

        // Then
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), message.getTime());
        assertEquals("C", message.getTemperatureUnit());
        assertNotNull(message.getSensorData());
        assertTrue(message.getSensorData().containsKey("DHT11"));
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        // Given
        String json = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C",
                    "UnknownField": "value",
                    "AnotherUnknown": 123
                }
                """;

        // When & Then
        assertDoesNotThrow(() -> objectMapper.readValue(json, SensorMessage.class));
    }

    @Test
    void shouldCaptureDynamicSensorData() throws Exception {
        // Given
        String json = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C",
                    "DHT11": {
                        "Temperature": 23.5,
                        "Humidity": 60.0
                    },
                    "DS18B20": {
                        "Id": "28-00000123abcd",
                        "Temperature": 25.0
                    }
                }
                """;

        // When
        SensorMessage message = objectMapper.readValue(json, SensorMessage.class);

        // Then
        Map<String, Object> sensorData = message.getSensorData();
        assertEquals(2, sensorData.size());
        assertTrue(sensorData.containsKey("DHT11"));
        assertTrue(sensorData.containsKey("DS18B20"));
    }

    @Test
    void shouldSetAndGetSensorIdAndLocation() {
        // Given
        SensorMessage message = new SensorMessage();
        String sensorId = "device-123";
        String location = "HOME_BATHROOM";

        // When
        message.setSensorId(sensorId);
        message.setLocation(location);

        // Then
        assertEquals(sensorId, message.getSensorId());
        assertEquals(location, message.getLocation());
    }

    @Test
    void shouldSetAndGetAvailability() {
        // Given
        SensorMessage message = new SensorMessage();
        String availability = "online";

        // When
        message.setAvailability(availability);

        // Then
        assertEquals(availability, message.getAvailability());
    }

    @Test
    void shouldAddSensorDataManually() {
        // Given
        SensorMessage message = new SensorMessage();

        // When
        message.addSensorData("CustomSensor", Map.of("value", 42));

        // Then
        Map<String, Object> sensorData = message.getSensorData();
        assertTrue(sensorData.containsKey("CustomSensor"));
        assertEquals(Map.of("value", 42), sensorData.get("CustomSensor"));
    }

    @Test
    void shouldHandleEmptySensorData() throws Exception {
        // Given
        String json = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C"
                }
                """;

        // When
        SensorMessage message = objectMapper.readValue(json, SensorMessage.class);

        // Then
        assertNotNull(message.getSensorData());
        assertTrue(message.getSensorData().isEmpty());
    }

    @Test
    void shouldReturnCorrectToString() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setTime(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
        message.setTemperatureUnit("C");
        message.setSensorId("device-1");
        message.setLocation("HOME_BATHROOM");
        message.addSensorData("TestSensor", "testValue");

        // When
        String result = message.toString();

        // Then
        assertTrue(result.contains("SensorMessage"));
        assertTrue(result.contains("time=2024-01-15T10:30:45"));
        assertTrue(result.contains("temperatureUnit='C'"));
        assertTrue(result.contains("sensorId='device-1'"));
        assertTrue(result.contains("location='HOME_BATHROOM'"));
        assertTrue(result.contains("sensorData="));
    }
}