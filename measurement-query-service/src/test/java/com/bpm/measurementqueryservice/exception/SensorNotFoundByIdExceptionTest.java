package com.bpm.measurementqueryservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorNotFoundByIdExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        Long sensorId = 1L;
        SensorNotFoundByIdException exception = new SensorNotFoundByIdException(sensorId);

        assertEquals("Sensor with id 1 not found", exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfException() {
        SensorNotFoundByIdException exception = new SensorNotFoundByIdException(1L);

        assertInstanceOf(Exception.class, exception);
    }
}
