package com.bpm.measurementqueryservice.repository;

import com.bpm.measurementqueryservice.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findSensorByLocation(String location);
}
