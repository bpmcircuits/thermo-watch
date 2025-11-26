package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DS18B20Data;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DS18B20MeasurementMappingStrategyTest {
    private final DS18B20MeasurementMappingStrategy strategy = new DS18B20MeasurementMappingStrategy();

    @Test
    void shouldMapDS18B20DataToMeasurementEvent() {
        // Given
        DS18B20Data data = buildDs18B20Data(22.3);
        String topic = "temp_bathroom";

        // When
        SensorMeasurementEvent event = strategy.toMeasurementEvent(topic, data);

        //Then
        assertEquals("DS18B20", event.sensorType());
        assertEquals(topic, event.location());
        assertEquals(new BigDecimal("22.3"), event.temperature());
        assertNotNull(event.sensorId());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldGenerateDifferentSensorIdForDifferentTopics() {
        // Given
        DS18B20Data data = buildDs18B20Data(22.3);
        String topic1 = "temp_bathroom";
        String topic2 = "temp_kitchen";

        // When
        String sensorId1 = strategy.toMeasurementEvent(topic1, data).sensorId();
        String sensorId2 = strategy.toMeasurementEvent(topic2, data).sensorId();

        // Then
        assertNotEquals(sensorId1, sensorId2);
    }

    private DS18B20Data buildDs18B20Data(double temperature) {
        DS18B20Data data = new DS18B20Data();
        ReflectionTestUtils.setField(data, "temperature", temperature);
        return data;
    }
}