package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorMessageParserTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SensorMessageParser parser;

    private String sensorPayload;
    private String sensorTopic;
    private String lwtTopic;

    @BeforeEach
    void setUp() {
        sensorPayload = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C",
                    "DHT11": {
                        "Temperature": 23.5,
                        "Humidity": 60.0
                    }
                }
                """;
        // zgodne z implementacją: tele/{site}/{room}/{deviceId}/SENSOR
        sensorTopic = "tele/HOME/BATHROOM/device-1/SENSOR";
        lwtTopic = "tele/HOME/BATHROOM/device-1/LWT";
    }

    @Test
    void shouldParseSensorMessage() throws Exception {
        // Given
        SensorMessage expectedMessage = new SensorMessage();
        when(objectMapper.readValue(sensorPayload, SensorMessage.class)).thenReturn(expectedMessage);

        // When
        SensorMessage result = parser.parse(sensorTopic, sensorPayload);

        // Then
        assertNotNull(result);
        assertEquals("device-1", result.getSensorId());
        assertEquals("HOME_BATHROOM", result.getLocation());
        verify(objectMapper).readValue(sensorPayload, SensorMessage.class);
    }

    @Test
    void shouldParseAvailabilityMessage() {
        // Given
        String availability = "Online";

        // When
        SensorMessage result = parser.parse(lwtTopic, availability);

        // Then
        assertNotNull(result);
        assertEquals("device-1", result.getSensorId());
        assertEquals("HOME_BATHROOM", result.getLocation());
        assertEquals("Online", result.getAvailability());
        verifyNoInteractions(objectMapper);
    }

    @Test
    void shouldDetectLwtTopicBySuffix() {
        // Given
        String availability = "Offline";

        // When
        SensorMessage result = parser.parse("tele/HOME/KITCHEN/device-2/LWT", availability);

        // Then
        assertNotNull(result);
        assertEquals("device-2", result.getSensorId());
        assertEquals("HOME_KITCHEN", result.getLocation());
        assertEquals("Offline", result.getAvailability());
    }

    @Test
    void shouldExtractLocationAndIdFromSensorTopic() throws Exception {
        // Given
        SensorMessage expectedMessage = new SensorMessage();
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class))).thenReturn(expectedMessage);

        // When
        SensorMessage result = parser.parse("tele/HOME/LIVINGROOM/device-3/SENSOR", sensorPayload);

        // Then
        assertEquals("device-3", result.getSensorId());
        assertEquals("HOME_LIVINGROOM", result.getLocation());
    }

    @Test
    void shouldExtractLocationFromLwtTopic() {
        // When
        SensorMessage result = parser.parse("tele/HOME/KITCHEN/device-4/LWT", "Online");

        // Then
        assertEquals("device-4", result.getSensorId());
        assertEquals("HOME_KITCHEN", result.getLocation());
    }

    @Test
    void shouldHandleTopicWithoutSlashesGracefully() throws Exception {
        // Given
        SensorMessage expectedMessage = new SensorMessage();
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class))).thenReturn(expectedMessage);

        // When
        SensorMessage result = parser.parse("invalidTopic", sensorPayload);

        // Then
        assertNull(result.getSensorId());
        assertNull(result.getLocation());
    }

    @Test
    void shouldTrimAvailabilityPayload() {
        // Given
        String availability = "  Online  ";

        // When
        SensorMessage result = parser.parse(lwtTopic, availability);

        // Then
        assertEquals("Online", result.getAvailability());
    }

    @Test
    void shouldThrowExceptionWhenSensorParsingFails() throws Exception {
        // Given
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class)))
                .thenThrow(new RuntimeException("JSON parse error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> parser.parse(sensorTopic, sensorPayload)
        );
        assertTrue(exception.getMessage().contains("Failed to parse sensor payload"));
    }

    @Test
    void shouldThrowExceptionWhenAvailabilityParsingFails() {
        // Given
        String invalidTopic = null;

        // When & Then
        assertThrows(
                RuntimeException.class,
                () -> parser.parse(invalidTopic, "Online")
        );
    }

    @Test
    void shouldHandleMultipleSensorMessages() throws Exception {
        // Given
        SensorMessage message1 = new SensorMessage();
        SensorMessage message2 = new SensorMessage();
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class)))
                .thenReturn(message1)
                .thenReturn(message2);

        // When
        SensorMessage result1 = parser.parse("tele/HOME/BATHROOM/device-1/SENSOR", sensorPayload);
        SensorMessage result2 = parser.parse("tele/HOME/KITCHEN/device-2/SENSOR", sensorPayload);

        // Then
        assertEquals("device-1", result1.getSensorId());
        assertEquals("HOME_BATHROOM", result1.getLocation());
        assertEquals("device-2", result2.getSensorId());
        assertEquals("HOME_KITCHEN", result2.getLocation());
        verify(objectMapper, times(2)).readValue(anyString(), eq(SensorMessage.class));
    }

    @Test
    void shouldHandleMultipleAvailabilityMessages() {
        // When
        SensorMessage result1 = parser.parse("tele/HOME/BATHROOM/device-1/LWT", "Online");
        SensorMessage result2 = parser.parse("tele/HOME/KITCHEN/device-2/LWT", "Offline");

        // Then
        assertEquals("device-1", result1.getSensorId());
        assertEquals("HOME_BATHROOM", result1.getLocation());
        assertEquals("Online", result1.getAvailability());

        assertEquals("device-2", result2.getSensorId());
        assertEquals("HOME_KITCHEN", result2.getLocation());
        assertEquals("Offline", result2.getAvailability());
    }
}