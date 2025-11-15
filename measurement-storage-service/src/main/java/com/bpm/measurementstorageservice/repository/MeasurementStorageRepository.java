package com.bpm.measurementstorageservice.repository;

import com.bpm.measurementstorageservice.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementStorageRepository extends JpaRepository<Measurement, Long> {
}
