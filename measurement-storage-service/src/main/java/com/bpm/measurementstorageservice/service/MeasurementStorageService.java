package com.bpm.measurementstorageservice.service;

import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.domain.RoomData;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.repository.MeasurementStorageRepository;
import com.bpm.measurementstorageservice.repository.RoomDataStorageRepository;
import com.bpm.measurementstorageservice.repository.SensorStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeasurementStorageService {

    private final MeasurementStorageRepository measurementRepo;
    private final SensorStorageRepository sensorRepo;
    private final RoomDataStorageRepository roomRepo;

    @Transactional
    public void store(SensorMeasurementEvent event) {

        RoomData room = roomRepo.findByLocation(event.location())
                .orElseGet(() -> RoomData.builder()
                        .location(event.location())
                        .build());
        room.setCurrentTemperature(event.temperature());
        room.setCurrentHumidity(event.humidity());
        roomRepo.save(room);

        Sensor sensor = sensorRepo.findBySensorId(event.sensorId())
                .orElseGet(() -> Sensor.builder()
                        .sensorId(event.sensorId())
                        .build());
        sensor.setSensorType(event.sensorType());
        sensor.setLocation(event.location());
        sensor.setLastSeen(event.timestamp());
        sensor.setIsOnline(true);
        sensor.setRoomData(room);

        Measurement m = Measurement.builder()
                .temperature(event.temperature())
                .humidity(event.humidity())
                .dewPoint(event.dewPoint())
                .timestamp(event.timestamp())
                .sensor(sensor)
                .build();

        sensorRepo.save(sensor);
        measurementRepo.save(m);
    }
}
