package com.bpm.measurementqueryservice.exception;

public class SensorNotFoundByIdException extends Exception {
    public SensorNotFoundByIdException(Long id) {
        super("Sensor with id " + id + " not found");
    }
}
