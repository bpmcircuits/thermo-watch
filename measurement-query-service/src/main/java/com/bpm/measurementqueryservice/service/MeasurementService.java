package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public List<Sensor> getAllSensors() {
        return measurementRepository.findAll();
    }
}
