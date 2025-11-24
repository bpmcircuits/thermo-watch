package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorMeasurementMapperTest {

    private SensorMeasurementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SensorMeasurementMapper();
    }

    @Test
    void shouldMapSensorDataToMeasurementEvent() {
        // Given
        String topic = "temp_bathroom";
        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent expectedEvent = new SensorMeasurementEvent(
                "DHT11",
                "sensor-001",
                topic,
                new BigDecimal("23.5"),
                new BigDecimal("60.0"),
                new BigDecimal("15.2"),
                LocalDateTime.now()
        );
        when(sensorData.toMeasurementEvent(topic)).thenReturn(expectedEvent);

        // When
        SensorMeasurementEvent result = mapper.mapToSensorMeasurementEvent(topic, sensorData);

        // Then
        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(sensorData, times(1)).toMeasurementEvent(topic);
    }

    @Test
    void shouldPassTopicToSensorData() {
        // Given
        String topic = "temp_livingroom";
        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(sensorData.toMeasurementEvent(topic)).thenReturn(event);

        // When
        mapper.mapToSensorMeasurementEvent(topic, sensorData);

        // Then
        verify(sensorData).toMeasurementEvent(topic);
    }

    @Test
    void shouldThrowExceptionWhenSensorDataIsNull() {
        // Given
        String topic = "temp_bathroom";
        SensorData sensorData = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapToSensorMeasurementEvent(topic, sensorData)
        );
        assertEquals("sensorData cannot be null", exception.getMessage());
    }

    @Test
    void shouldHandleDifferentTopics() {
        // Given
        SensorData sensorData = mock(SensorData.class);
        String topic1 = "temp_bathroom";
        String topic2 = "temp_kitchen";
        SensorMeasurementEvent event1 = mock(SensorMeasurementEvent.class);
        SensorMeasurementEvent event2 = mock(SensorMeasurementEvent.class);
        when(sensorData.toMeasurementEvent(topic1)).thenReturn(event1);
        when(sensorData.toMeasurementEvent(topic2)).thenReturn(event2);

        // When
        SensorMeasurementEvent result1 = mapper.mapToSensorMeasurementEvent(topic1, sensorData);
        SensorMeasurementEvent result2 = mapper.mapToSensorMeasurementEvent(topic2, sensorData);

        // Then
        assertEquals(event1, result1);
        assertEquals(event2, result2);
        verify(sensorData).toMeasurementEvent(topic1);
        verify(sensorData).toMeasurementEvent(topic2);
    }

    @Test
    void shouldHandleNullTopic() {
        // Given
        String topic = null;
        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(sensorData.toMeasurementEvent(topic)).thenReturn(event);

        // When
        SensorMeasurementEvent result = mapper.mapToSensorMeasurementEvent(topic, sensorData);

        // Then
        assertNotNull(result);
        verify(sensorData).toMeasurementEvent(null);
    }
}
