package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DHT11MeasurementMappingStrategyTest {

    private final DHT11MeasurementMappingStrategy strategy = new DHT11MeasurementMappingStrategy();

    @Test
    void shouldMapDht11DataToMeasurementEvent() {
        // Given
        DHT11Data data = buildDht11Data(25.0, 55.0, 14.5);
        SensorMessage message = new SensorMessage();
        message.setSensorId("sensor-123");
        message.setLocation("HOME_BATHROOM");

        // When
        SensorMeasurementEvent event = strategy.toMeasurementEvent(message, data);

        // Then
        assertEquals("DHT11", event.sensorType());
        assertEquals("sensor-123", event.sensorId());
        assertEquals("HOME_BATHROOM", event.location());
        assertEquals(new BigDecimal("25.0"), event.temperature());
        assertEquals(new BigDecimal("55.0"), event.humidity());
        assertEquals(new BigDecimal("14.5"), event.dewPoint());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldUseDifferentMetadataForDifferentMessages() {
        // Given
        DHT11Data data = buildDht11Data(25.0, 55.0, 14.5);

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

    private DHT11Data buildDht11Data(double temperature, double humidity, double dewPoint) {
        DHT11Data data = new DHT11Data();
        ReflectionTestUtils.setField(data, "temperature", temperature);
        ReflectionTestUtils.setField(data, "humidity", humidity);
        ReflectionTestUtils.setField(data, "dewPoint", dewPoint);
        return data;
    }
}