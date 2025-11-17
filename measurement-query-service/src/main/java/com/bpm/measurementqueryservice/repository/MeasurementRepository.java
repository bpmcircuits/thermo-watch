package com.bpm.measurementqueryservice.repository;

import com.bpm.measurementqueryservice.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Sensor, Long> {
}
