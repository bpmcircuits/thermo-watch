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
        sensorMessage.setSensorId("sensor-1");
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
        verify(sensorMeasurementService).sendAvailability("sensor-1", "Online");
    }

    @Test
    void shouldProcessOfflineAvailability() {
        // Given
        sensorMessage.setAvailability("Offline");

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("sensor-1", "Offline");
    }

    @Test
    void shouldUseSensorIdFromMessage() {
        // Given
        sensorMessage.setSensorId("sensor-2");
        sensorMessage.setAvailability("Online");

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("sensor-2", "Online");
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorId("sensor-1");
        message1.setAvailability("Online");

        SensorMessage message2 = new SensorMessage();
        message2.setSensorId("sensor-2");
        message2.setAvailability("Offline");

        // When
        strategy.processSensorData(null, message1);
        strategy.processSensorData(null, message2);

        // Then
        verify(sensorMeasurementService).sendAvailability("sensor-1", "Online");
        verify(sensorMeasurementService).sendAvailability("sensor-2", "Offline");
    }

    @Test
    void shouldIgnoreDataParameter() {
        // Given
        sensorMessage.setAvailability("Online");
        String someData = "";

        // When
        strategy.processSensorData(someData, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("sensor-1", "Online");
    }

    @Test
    void shouldHandleNullAvailability() {
        // Given
        sensorMessage.setAvailability(null);

        // When
        strategy.processSensorData(null, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendAvailability("sensor-1", null);
    }
}