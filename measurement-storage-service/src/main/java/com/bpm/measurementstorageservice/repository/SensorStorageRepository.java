package com.bpm.measurementstorageservice.repository;

import com.bpm.measurementstorageservice.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorStorageRepository extends JpaRepository<Sensor, Long> {
}
