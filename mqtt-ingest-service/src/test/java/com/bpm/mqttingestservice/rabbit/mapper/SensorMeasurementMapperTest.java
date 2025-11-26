package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorMeasurementMapperTest {

    private SensorMeasurementMappingStrategy<DHT11Data> strategy;
    private SensorMeasurementMapper mapper;

    @BeforeEach
    void setUp() {
        strategy = mock(SensorMeasurementMappingStrategy.class);
        when(strategy.getSupportedType()).thenAnswer(invocation -> DHT11Data.class);

        mapper = new SensorMeasurementMapper(List.of(strategy));
    }

    @Test
    void shouldDelegateToStrategy() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setSensorId("sensor-1");
        message.setLocation("HOME_BATHROOM");

        DHT11Data data = mock(DHT11Data.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(strategy.toMeasurementEvent(message, data)).thenReturn(event);

        // When
        SensorMeasurementEvent result = mapper.mapToSensorMeasurementEvent(message, data);

        // Then
        assertEquals(event, result);
        verify(strategy).toMeasurementEvent(message, data);
    }

    @Test
    void shouldThrowWhenSensorDataIsNull() {
        // Given
        SensorMessage message = new SensorMessage();

        // When & Then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapToSensorMeasurementEvent(message, null)
        );
        assertEquals("sensorData cannot be null", ex.getMessage());
    }
}