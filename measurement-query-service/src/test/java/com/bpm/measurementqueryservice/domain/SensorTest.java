package com.bpm.measurementqueryservice.domain;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SensorTest {

    private Sensor createSensor() throws Exception {
        Constructor<Sensor> constructor = Sensor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Sensor sensor = constructor.newInstance();

        setField(sensor, "id", 1L);
        setField(sensor, "sensorId", "TEST-001");
        setField(sensor, "sensorType", "DHT22");
        setField(sensor, "location", "Kitchen");
        setField(sensor, "timestamp", "2024-01-01T12:00:00");
        setField(sensor, "lastSeen", LocalDateTime.of(2024, 1, 1, 12, 30));
        setField(sensor, "isOnline", true);
        setField(sensor, "roomDataId", 1L);

        return sensor;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldCreateSensorWithAllFields() throws Exception {
        Sensor sensor = createSensor();

        assertEquals(1L, sensor.getId());
        assertEquals("TEST-001", sensor.getSensorId());
        assertEquals("DHT22", sensor.getSensorType());
        assertEquals("Kitchen", sensor.getLocation());
        assertEquals("2024-01-01T12:00:00", sensor.getTimestamp());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 30), sensor.getLastSeen());
        assertTrue(sensor.getIsOnline());
        assertEquals(1L, sensor.getRoomDataId());
    }

    @Test
    void shouldHandleOfflineSensor() throws Exception {
        Sensor sensor = createSensor();
        setField(sensor, "isOnline", false);

        assertFalse(sensor.getIsOnline());
    }
}
