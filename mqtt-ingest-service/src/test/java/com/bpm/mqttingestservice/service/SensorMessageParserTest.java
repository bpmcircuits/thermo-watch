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
        sensorTopic = "tele/temp_bathroom/SENSOR";
        lwtTopic = "tele/temp_bathroom/LWT";
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
        assertEquals("temp_bathroom", result.getSensorTopic());
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
        assertEquals("temp_bathroom", result.getSensorTopic());
        assertEquals("Online", result.getAvailability());
        verifyNoInteractions(objectMapper);
    }

    @Test
    void shouldDetectLwtTopicBySuffix() {
        // Given
        String availability = "Offline";

        // When
        SensorMessage result = parser.parse("tele/temp_kitchen/LWT", availability);

        // Then
        assertNotNull(result);
        assertEquals("temp_kitchen", result.getSensorTopic());
        assertEquals("Offline", result.getAvailability());
    }

    @Test
    void shouldExtractLocationFromSensorTopic() throws Exception {
        // Given
        SensorMessage expectedMessage = new SensorMessage();
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class))).thenReturn(expectedMessage);

        // When
        SensorMessage result = parser.parse("tele/temp_livingroom/SENSOR", sensorPayload);

        // Then
        assertEquals("temp_livingroom", result.getSensorTopic());
    }

    @Test
    void shouldExtractLocationFromLwtTopic() {
        // When
        SensorMessage result = parser.parse("tele/temp_kitchen/LWT", "Online");

        // Then
        assertEquals("temp_kitchen", result.getSensorTopic());
    }

    @Test
    void shouldHandleTopicWithoutSlashes() throws Exception {
        // Given
        SensorMessage expectedMessage = new SensorMessage();
        when(objectMapper.readValue(anyString(), eq(SensorMessage.class))).thenReturn(expectedMessage);

        // When
        SensorMessage result = parser.parse("temp_bathroom", sensorPayload);

        // Then
        assertEquals("temp_bathroom", result.getSensorTopic());
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
        SensorMessage result1 = parser.parse("tele/temp_bathroom/SENSOR", sensorPayload);
        SensorMessage result2 = parser.parse("tele/temp_kitchen/SENSOR", sensorPayload);

        // Then
        assertEquals("temp_bathroom", result1.getSensorTopic());
        assertEquals("temp_kitchen", result2.getSensorTopic());
        verify(objectMapper, times(2)).readValue(anyString(), eq(SensorMessage.class));
    }

    @Test
    void shouldHandleMultipleAvailabilityMessages() {
        // When
        SensorMessage result1 = parser.parse("tele/temp_bathroom/LWT", "Online");
        SensorMessage result2 = parser.parse("tele/temp_kitchen/LWT", "Offline");

        // Then
        assertEquals("temp_bathroom", result1.getSensorTopic());
        assertEquals("Online", result1.getAvailability());
        assertEquals("temp_kitchen", result2.getSensorTopic());
        assertEquals("Offline", result2.getAvailability());
    }
}
