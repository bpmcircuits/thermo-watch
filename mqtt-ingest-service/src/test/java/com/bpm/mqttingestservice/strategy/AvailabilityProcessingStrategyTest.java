package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityProcessingStrategyTest {

    @Mock
    private SensorMeasurementService sensorMeasurementService;

    @InjectMocks
    private AvailabilityProcessingStrategy strategy;

    private SensorMessage sensorMessage;

    @BeforeEach
    void setUp() {
        sensorMessage = new SensorMessage();
        sensorMessage.setSensorTopic("temp_bathroom");
    }

    @Test
    void shouldReturnAvailabilitySensorType() {
        // When
        String result = strategy.getSensorType();

        // Then
        assertEquals("AVAILABILITY", result);
    }

    @Test
    void shouldReturnStringDataClass() {
        // When
        Class<?> result = strategy.getDataClass();

        // Then
        assertEquals(String.class, result);
    }

    @Test
    void shouldProcessOnlineAvailability() {
        // Given
        sensorMessage.setAvailability("Online");

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_bathroom", "Online");
    }

    @Test
    void shouldProcessOfflineAvailability() {
        // Given
        sensorMessage.setAvailability("Offline");

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_bathroom", "Offline");
    }

    @Test
    void shouldUseTopicFromMessage() {
        // Given
        sensorMessage.setSensorTopic("temp_kitchen");
        sensorMessage.setAvailability("Online");

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_kitchen", "Online");
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorTopic("temp_bathroom");
        message1.setAvailability("Online");

        SensorMessage message2 = new SensorMessage();
        message2.setSensorTopic("temp_kitchen");
        message2.setAvailability("Offline");

        // When
        strategy.processSensorData(null, message1);
        strategy.processSensorData(null, message2);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_bathroom", "Online");
        verify(sensorMeasurementService).sendAvailability("temp_kitchen", "Offline");
    }

    @Test
    void shouldIgnoreDataParameter() {
        // Given
        sensorMessage.setAvailability("Online");
        Object someData = new Object();

        // When
        strategy.processSensorData(someData, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_bathroom", "Online");
    }

    @Test
    void shouldHandleNullAvailability() {
        // Given
        sensorMessage.setAvailability(null);

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("temp_bathroom", null);
    }
}
