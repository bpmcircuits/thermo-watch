package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorData;
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
        String topic = "temp_bathroom";
        DHT11Data data = mock(DHT11Data.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(strategy.toMeasurementEvent(topic, data)).thenReturn(event);

        // When
        SensorMeasurementEvent result = mapper.mapToSensorMeasurementEvent(topic, data);

        // Then
        assertEquals(event, result);
        verify(strategy).toMeasurementEvent(topic, data);
    }

    @Test
    void shouldThrowWhenSensorDataIsNull() {
        // When & Then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapToSensorMeasurementEvent("topic", null)
        );
        assertEquals("sensorData cannot be null", ex.getMessage());
    }
}
