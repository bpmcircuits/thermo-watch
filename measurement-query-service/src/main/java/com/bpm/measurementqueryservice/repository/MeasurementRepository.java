package com.bpm.measurementqueryservice.repository;

import com.bpm.measurementqueryservice.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    @Query
    List<Measurement> findMeasurementsBySensorFkForPeriodOfTime(@Param("ID") Long sensorFk, @Param("AFTER")LocalDateTime after);
}
