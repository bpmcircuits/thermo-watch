package com.bpm.measurementqueryservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorNotFoundByLocationExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        String location = "Kitchen";
        SensorNotFoundByLocationException exception = new SensorNotFoundByLocationException(location);

        assertEquals("Sensor with location Kitchen not found", exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfException() {
        SensorNotFoundByLocationException exception = new SensorNotFoundByLocationException("Kitchen");

        assertInstanceOf(Exception.class, exception);
    }
}
