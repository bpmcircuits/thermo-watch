package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DHT11Data;
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
        String topic = "temp_bathroom";

        // When
        SensorMeasurementEvent event = strategy.toMeasurementEvent(topic, data);

        // Then
        assertEquals("DHT11", event.sensorType());
        assertEquals(topic, event.location());
        assertEquals(new BigDecimal("25.0"), event.temperature());
        assertEquals(new BigDecimal("55.0"), event.humidity());
        assertEquals(new BigDecimal("14.5"), event.dewPoint());
        assertNotNull(event.sensorId());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldGenerateDifferentSensorIdForDifferentTopics() {
        // Given
        DHT11Data data = buildDht11Data(25.0, 55.0, 14.5);
        String topic1 = "temp_bathroom";
        String topic2 = "temp_livingroom";

        // When
        String sensorId1 = strategy.toMeasurementEvent(topic1, data).sensorId();
        String sensorId2 = strategy.toMeasurementEvent(topic2, data).sensorId();

        // Then
        assertNotEquals(sensorId1, sensorId2);
    }

    private DHT11Data buildDht11Data(double temperature, double humidity, double dewPoint) {
        DHT11Data data = new DHT11Data();
        ReflectionTestUtils.setField(data, "temperature", temperature);
        ReflectionTestUtils.setField(data, "humidity", humidity);
        ReflectionTestUtils.setField(data, "dewPoint", dewPoint);
        return data;
    }
}