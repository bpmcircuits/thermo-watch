package com.bpm.measurementstorageservice.mapper;

import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import org.springframework.stereotype.Service;

@Service
public class MeasurementStorageMapper {

    public Measurement mapToMeasurement(SensorMeasurementEvent sensorMeasurementEvent) {
        return Measurement.builder()
                .sensorType(sensorMeasurementEvent.sensorType())
                .sensorId(sensorMeasurementEvent.sensorId())
                .location(sensorMeasurementEvent.location())
                .temperature(sensorMeasurementEvent.temperature())
                .humidity(sensorMeasurementEvent.humidity())
                .dewPoint(sensorMeasurementEvent.dewPoint())
                .timestamp(sensorMeasurementEvent.timestamp())
                .build();
    }
}
