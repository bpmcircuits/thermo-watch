package com.bpm.measurementqueryservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementNotFoundBySensorIdExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        Long sensorId = 1L;
        MeasurementNotFoundBySensorIdException exception = new MeasurementNotFoundBySensorIdException(sensorId);

        assertEquals("Measurement with sensor id 1 not found", exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfException() {
        MeasurementNotFoundBySensorIdException exception = new MeasurementNotFoundBySensorIdException(1L);

        assertInstanceOf(Exception.class, exception);
    }
}
