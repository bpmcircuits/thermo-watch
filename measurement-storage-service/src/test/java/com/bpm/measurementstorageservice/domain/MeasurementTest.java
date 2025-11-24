package com.bpm.measurementstorageservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementTest {

    @Test
    void shouldCreateMeasurementWithBuilder() {
        // Given
        BigDecimal temperature = new BigDecimal("25.50");
        BigDecimal humidity = new BigDecimal("60.00");
        BigDecimal dewPoint = new BigDecimal("17.30");
        LocalDateTime timestamp = LocalDateTime.now();
        Sensor sensor = Sensor.builder().sensorId("sensor-1").build();

        // When
        Measurement measurement = Measurement.builder()
                .temperature(temperature)
                .humidity(humidity)
                .dewPoint(dewPoint)
                .timestamp(timestamp)
                .sensor(sensor)
                .build();

        // Then
        assertNotNull(measurement);
        assertEquals(temperature, measurement.getTemperature());
        assertEquals(humidity, measurement.getHumidity());
        assertEquals(dewPoint, measurement.getDewPoint());
        assertEquals(timestamp, measurement.getTimestamp());
        assertEquals(sensor, measurement.getSensor());
    }

    @Test
    void shouldAttachToSensor() {
        // Given
        Measurement measurement = Measurement.builder().build();
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .measurements(new ArrayList<>())
                .build();

        // When
        measurement.attachTo(sensor);

        // Then
        assertEquals(sensor, measurement.getSensor());
        assertTrue(sensor.getMeasurements().contains(measurement));
    }

    @Test
    void shouldNotAddDuplicateMeasurementToSensor() {
        // Given
        Measurement measurement = Measurement.builder().build();
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .measurements(new ArrayList<>())
                .build();
        measurement.attachTo(sensor);

        // When
        measurement.attachTo(sensor);

        // Then
        assertEquals(1, sensor.getMeasurements().size());
    }

    @Test
    void shouldHandleNullMeasurementsListWhenAttaching() {
        // Given
        Measurement measurement = Measurement.builder().build();
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();

        // When
        measurement.attachTo(sensor);

        // Then
        assertEquals(sensor, measurement.getSensor());
        assertNull(sensor.getMeasurements());
    }
}
