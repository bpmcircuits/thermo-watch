package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Optional<Sensor> getSensorById(Long id) {
        return sensorRepository.findById(id);
    }

    public Optional<Sensor> getSensorByLocation(String location) {
        return sensorRepository.findSensorByLocation(location);
    }
}
