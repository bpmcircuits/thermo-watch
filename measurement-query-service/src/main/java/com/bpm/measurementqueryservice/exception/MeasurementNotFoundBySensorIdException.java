package com.bpm.measurementqueryservice.exception;

public class MeasurementNotFoundBySensorIdException extends  Exception {
    public MeasurementNotFoundBySensorIdException(Long sensorId) {
        super("Measurement with sensor id " + sensorId + " not found");
    }
}
