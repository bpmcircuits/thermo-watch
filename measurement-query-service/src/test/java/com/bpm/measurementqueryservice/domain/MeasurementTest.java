package com.bpm.measurementqueryservice.domain;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementTest {

    private Measurement createMeasurement() throws Exception {
        Constructor<Measurement> constructor = Measurement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Measurement measurement = constructor.newInstance();

        setField(measurement, "id", 1L);
        setField(measurement, "sensorId", 1L);
        setField(measurement, "temperature", new BigDecimal("22.50"));
        setField(measurement, "humidity", new BigDecimal("45.00"));
        setField(measurement, "dewPoint", new BigDecimal("10.50"));
        setField(measurement, "timestamp", LocalDateTime.of(2024, 1, 1, 12, 0));

        return measurement;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldCreateMeasurementWithAllFields() throws Exception {
        Measurement measurement = createMeasurement();

        assertEquals(1L, measurement.getId());
        assertEquals(1L, measurement.getSensorId());
        assertEquals(new BigDecimal("22.50"), measurement.getTemperature());
        assertEquals(new BigDecimal("45.00"), measurement.getHumidity());
        assertEquals(new BigDecimal("10.50"), measurement.getDewPoint());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), measurement.getTimestamp());
    }

    @Test
    void shouldHandleNegativeTemperature() throws Exception {
        Measurement measurement = createMeasurement();
        setField(measurement, "temperature", new BigDecimal("-5.50"));

        assertEquals(new BigDecimal("-5.50"), measurement.getTemperature());
    }

    @Test
    void shouldHandleHighHumidity() throws Exception {
        Measurement measurement = createMeasurement();
        setField(measurement, "humidity", new BigDecimal("99.99"));

        assertEquals(new BigDecimal("99.99"), measurement.getHumidity());
    }
}
