package com.bpm.measurementqueryservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementNotFoundByLocationExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        String location = "Bedroom";
        MeasurementNotFoundByLocationException exception = new MeasurementNotFoundByLocationException(location);

        assertEquals("Measurement with location Bedroom not found", exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfException() {
        MeasurementNotFoundByLocationException exception = new MeasurementNotFoundByLocationException("Bedroom");

        assertInstanceOf(Exception.class, exception);
    }
}
