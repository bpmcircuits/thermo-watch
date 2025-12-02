package com.bpm.events.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SensorMeasurementEventTest {

    @Test
    void shouldCreateSensorMeasurementEventWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT22")
                .sensorId("TEST-001")
                .location("Kitchen")
                .temperature(new BigDecimal("22.50"))
                .humidity(new BigDecimal("45.00"))
                .pressure(new BigDecimal("1013.25"))
                .dewPoint(new BigDecimal("10.50"))
                .timestamp(timestamp)
                .build();

        assertEquals("DHT22", event.sensorType());
        assertEquals("TEST-001", event.sensorId());
        assertEquals("Kitchen", event.location());
        assertEquals(new BigDecimal("22.50"), event.temperature());
        assertEquals(new BigDecimal("45.00"), event.humidity());
        assertEquals(new BigDecimal("10.50"), event.dewPoint());
        assertEquals(new BigDecimal("1013.25"), event.pressure());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    void shouldHandleNegativeTemperature() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("TEST-002")
                .location("Basement")
                .temperature(new BigDecimal("-5.50"))
                .humidity(new BigDecimal("80.00"))
                .dewPoint(new BigDecimal("-8.00"))
                .timestamp(LocalDateTime.now())
                .build();

        assertEquals(new BigDecimal("-5.50"), event.temperature());
        assertEquals(new BigDecimal("-8.00"), event.dewPoint());
    }

    @Test
    void shouldBeEqualWhenFieldsAreTheSame() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        SensorMeasurementEvent event1 = SensorMeasurementEvent.builder()
                .sensorType("DHT22")
                .sensorId("TEST-001")
                .location("Kitchen")
                .temperature(new BigDecimal("22.50"))
                .humidity(new BigDecimal("45.00"))
                .dewPoint(new BigDecimal("10.50"))
                .pressure(new BigDecimal("1013.25"))
                .timestamp(timestamp)
                .build();

        SensorMeasurementEvent event2 = SensorMeasurementEvent.builder()
                .sensorType("DHT22")
                .sensorId("TEST-001")
                .location("Kitchen")
                .temperature(new BigDecimal("22.50"))
                .humidity(new BigDecimal("45.00"))
                .dewPoint(new BigDecimal("10.50"))
                .pressure(new BigDecimal("1013.25"))
                .timestamp(timestamp)
                .build();

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
