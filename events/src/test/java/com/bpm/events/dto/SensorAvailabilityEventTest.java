package com.bpm.events.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SensorAvailabilityEventTest {

    @Test
    void shouldCreateSensorAvailabilityEventWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorLocation("Kitchen")
                .status("ONLINE")
                .source("sensor-service")
                .timestamp(timestamp)
                .build();

        assertEquals("Kitchen", event.sensorId());
        assertEquals("ONLINE", event.status());
        assertEquals("sensor-service", event.source());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    void shouldHandleOfflineStatus() {
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorLocation("Bedroom")
                .status("OFFLINE")
                .source("monitoring-service")
                .timestamp(LocalDateTime.now())
                .build();

        assertEquals("OFFLINE", event.status());
    }

    @Test
    void shouldBeEqualWhenFieldsAreTheSame() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        SensorAvailabilityEvent event1 = SensorAvailabilityEvent.builder()
                .sensorLocation("Kitchen")
                .status("ONLINE")
                .source("sensor-service")
                .timestamp(timestamp)
                .build();

        SensorAvailabilityEvent event2 = SensorAvailabilityEvent.builder()
                .sensorLocation("Kitchen")
                .status("ONLINE")
                .source("sensor-service")
                .timestamp(timestamp)
                .build();

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
