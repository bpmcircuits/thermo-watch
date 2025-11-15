package com.bpm.measurementstorageservice.service;

import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.repository.MeasurementStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasurementStorageService {

    private final MeasurementStorageRepository measurementStorageRepository;

    public void store(Measurement measurement) {
        measurementStorageRepository.save(measurement);
    }
}
