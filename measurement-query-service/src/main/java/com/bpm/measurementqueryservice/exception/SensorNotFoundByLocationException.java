package com.bpm.measurementqueryservice.exception;

public class SensorNotFoundByLocationException extends Exception {
    public SensorNotFoundByLocationException(String location) {
        super("Sensor with location " + location + " not found");
    }
}
