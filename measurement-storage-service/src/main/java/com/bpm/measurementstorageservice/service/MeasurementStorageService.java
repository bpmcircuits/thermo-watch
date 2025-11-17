package com.bpm.measurementstorageservice.service;

import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.domain.RoomData;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.repository.MeasurementStorageRepository;
import com.bpm.measurementstorageservice.repository.RoomDataStorageRepository;
import com.bpm.measurementstorageservice.repository.SensorStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasurementStorageService {

    private final MeasurementStorageRepository measurementStorageRepository;
    private final SensorStorageRepository sensorStorageRepository;
    private final RoomDataStorageRepository roomDataStorageRepository;

    public void storeSensorData(Sensor sensor) {
        sensorStorageRepository.save(sensor);
    }

    public void storeMeasurementData(Measurement measurement) {
        measurementStorageRepository.save(measurement);
    }

    public void storeRoomData(RoomData roomData) {
        roomDataStorageRepository.save(roomData);
    }
}
