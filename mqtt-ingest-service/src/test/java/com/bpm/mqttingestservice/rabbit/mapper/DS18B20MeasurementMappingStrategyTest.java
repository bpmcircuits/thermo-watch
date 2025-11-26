package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DS18B20MeasurementMappingStrategyTest {
    private final DS18B20MeasurementMappingStrategy strategy = new DS18B20MeasurementMappingStrategy();

    @Test
    void shouldMapDS18B20DataToMeasurementEvent() {
        // Given
        DS18B20Data data = buildDs18B20Data("28-00000123abcd", 22.3);
        SensorMessage message = new SensorMessage();
        message.setSensorId("sensor-xyz");
        message.setLocation("HOME_BATHROOM");

        // When
        SensorMeasurementEvent event = strategy.toMeasurementEvent(message, data);

        //Then
        assertEquals("DS18B20", event.sensorType());
        assertEquals("sensor-xyz", event.sensorId());
        assertEquals("HOME_BATHROOM", event.location());
        assertEquals(new BigDecimal("22.3"), event.temperature());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldUseDifferentMetadataForDifferentMessages() {
        // Given
        DS18B20Data data = buildDs18B20Data("28-00000123abcd", 22.3);

        SensorMessage message1 = new SensorMessage();
        message1.setSensorId("sensor-1");
        message1.setLocation("HOME_BATHROOM");

        SensorMessage message2 = new SensorMessage();
        message2.setSensorId("sensor-2");
        message2.setLocation("HOME_KITCHEN");

        // When
        SensorMeasurementEvent event1 = strategy.toMeasurementEvent(message1, data);
        SensorMeasurementEvent event2 = strategy.toMeasurementEvent(message2, data);

        // Then
        assertNotEquals(event1.sensorId(), event2.sensorId());
        assertNotEquals(event1.location(), event2.location());
    }

    private DS18B20Data buildDs18B20Data(String id, double temperature) {
        DS18B20Data data = new DS18B20Data();
        ReflectionTestUtils.setField(data, "id", id);
        ReflectionTestUtils.setField(data, "temperature", temperature);
        return data;
    }
}