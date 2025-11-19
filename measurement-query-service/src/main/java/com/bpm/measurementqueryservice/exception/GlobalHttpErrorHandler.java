package com.bpm.measurementqueryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalHttpErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(SensorNotFoundByIdException.class)
    public ResponseEntity<String> handleSensorNotFoundByIdException(SensorNotFoundByIdException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SensorNotFoundByLocationException.class)
    public ResponseEntity<String> handleSensorNotFoundByLocationException(
            SensorNotFoundByLocationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MeasurementNotFoundBySensorIdException.class)
    public ResponseEntity<String> handleMeasurementNotFoundBySensorIdException(
            MeasurementNotFoundBySensorIdException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MeasurementNotFoundByLocationException.class)
    public ResponseEntity<String> handleMeasurementNotFoundByLocationException(
            MeasurementNotFoundByLocationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
