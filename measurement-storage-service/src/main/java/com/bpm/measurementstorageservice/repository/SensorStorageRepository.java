package com.bpm.measurementstorageservice.repository;

import com.bpm.measurementstorageservice.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorStorageRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorId(String sensorId);
    boolean existsBySensorId(String sensorId);
    long countByLocation(String location);
}
