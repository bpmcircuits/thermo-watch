package com.bpm.measurementstorageservice.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.repository.SensorStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvailabilityStorageService {
    private final SensorStorageRepository sensorRepo;

    @Transactional
    public void storeAvailability(SensorAvailabilityEvent event) {
        Sensor sensor = sensorRepo.findBySensorId(event.sensorId()).orElse(null);

        if (sensor != null) {
            sensor.setIsOnline(event.status().equalsIgnoreCase("ONLINE"));
            sensor.setLastSeen(event.timestamp());
            sensorRepo.save(sensor);
        }
    }
}
