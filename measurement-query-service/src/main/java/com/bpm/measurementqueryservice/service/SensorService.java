package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByIdException;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Sensor getSensorById(Long id) throws SensorNotFoundByIdException {
        return sensorRepository.findById(id).orElseThrow(() -> new SensorNotFoundByIdException(id));
    }

    public Sensor getSensorByLocation(String location) throws SensorNotFoundByLocationException {
        return sensorRepository.findSensorByLocation(location).orElseThrow(() ->
                new SensorNotFoundByLocationException(location));
    }
}
