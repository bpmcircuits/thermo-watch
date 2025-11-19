package com.bpm.measurementqueryservice.exception;

public class MeasurementNotFoundByLocationException extends Exception {
    public MeasurementNotFoundByLocationException(String location) {
        super("Measurement with location " + location + " not found");
    }
}
