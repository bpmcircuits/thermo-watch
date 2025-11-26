package com.bpm.measurementstorageservice.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.domain.RoomData;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.repository.MeasurementStorageRepository;
import com.bpm.measurementstorageservice.repository.RoomDataStorageRepository;
import com.bpm.measurementstorageservice.repository.SensorStorageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeasurementStorageService {

    private final static Logger log = LoggerFactory.getLogger(MeasurementStorageService.class);

    private final MeasurementStorageRepository measurementRepo;
    private final SensorStorageRepository sensorRepo;
    private final RoomDataStorageRepository roomRepo;

    @Transactional
    public void storeMeasurement(SensorMeasurementEvent event) {

        RoomData room = roomRepo.findByLocation(event.location())
                .orElseGet(() -> RoomData.builder()
                        .location(event.location())
                        .build());

        room.setCurrentTemperature(event.temperature());
        room.setCurrentHumidity(event.humidity());
        room.setSensorCount(countSensorsByLocation(event.location()));
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

    @Transactional
    public void storeAvailability(SensorAvailabilityEvent event) {
        Sensor sensor = sensorRepo.findBySensorId(event.sensorId()).orElse(null);

        if (sensor != null) {
            sensor.setIsOnline(event.status().equalsIgnoreCase("ONLINE"));
            sensor.setLastSeen(event.timestamp());
            sensorRepo.save(sensor);
        }
    }

    private Integer countSensorsByLocation(String location) {
        return sensorRepo.countByLocation(location);
    }
}
