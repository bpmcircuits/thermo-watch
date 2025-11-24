package com.bpm.measurementqueryservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GlobalHttpErrorHandlerTest {

    private GlobalHttpErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalHttpErrorHandler();
    }

    @Test
    void shouldHandleSensorNotFoundByIdException() {
        SensorNotFoundByIdException exception = new SensorNotFoundByIdException(1L);

        ResponseEntity<String> response = handler.handleSensorNotFoundByIdException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Sensor with id 1 not found", response.getBody());
    }

    @Test
    void shouldHandleSensorNotFoundByLocationException() {
        SensorNotFoundByLocationException exception = new SensorNotFoundByLocationException("Kitchen");

        ResponseEntity<String> response = handler.handleSensorNotFoundByLocationException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Sensor with location Kitchen not found", response.getBody());
    }

    @Test
    void shouldHandleMeasurementNotFoundBySensorIdException() {
        MeasurementNotFoundBySensorIdException exception = new MeasurementNotFoundBySensorIdException(1L);

        ResponseEntity<String> response = handler.handleMeasurementNotFoundBySensorIdException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Measurement with sensor id 1 not found", response.getBody());
    }

    @Test
    void shouldHandleMeasurementNotFoundByLocationException() {
        MeasurementNotFoundByLocationException exception = new MeasurementNotFoundByLocationException("Bedroom");

        ResponseEntity<String> response = handler.handleMeasurementNotFoundByLocationException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Measurement with location Bedroom not found", response.getBody());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException exception = new ConstraintViolationException("Invalid parameter", new HashSet<>());

        ResponseEntity<String> response = handler.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid request parameter"));
    }
}
