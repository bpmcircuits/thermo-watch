package com.bpm.measurementstorageservice.mapper;

import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.domain.RoomData;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import org.springframework.stereotype.Service;

@Service
public class MeasurementStorageMapper {


    public Sensor mapToSensor(SensorMeasurementEvent sensorMeasurementEvent) {
        return Sensor.builder()
                .sensorId(sensorMeasurementEvent.sensorId())
                .location(sensorMeasurementEvent.location())
                .sensorType(sensorMeasurementEvent.sensorType())
                .lastSeen(sensorMeasurementEvent.timestamp())
                .isOnline(true) // assume sensor is online when a measurement is received
                .build();
    }

    public Measurement mapToMeasurement(SensorMeasurementEvent sensorMeasurementEvent) {
        return Measurement.builder()
                .sensorId(sensorMeasurementEvent.sensorId())
                .temperature(sensorMeasurementEvent.temperature())
                .humidity(sensorMeasurementEvent.humidity())
                .dewPoint(sensorMeasurementEvent.dewPoint())
                .timestamp(sensorMeasurementEvent.timestamp())
                .build();
    }

    public RoomData mapToRoomData(SensorMeasurementEvent sensorMeasurementEvent) {
        return RoomData.builder()
                .location(sensorMeasurementEvent.location())
                .currentTemperature(sensorMeasurementEvent.temperature())
                .currentHumidity(sensorMeasurementEvent.humidity())
                .sensorCount(null) // to do: calculate sensor count
                .build();
    }
}
