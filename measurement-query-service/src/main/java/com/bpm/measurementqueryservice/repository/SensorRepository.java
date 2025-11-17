package com.bpm.measurementqueryservice.repository;

import com.bpm.measurementqueryservice.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    @Query
    List<Sensor> findSesorBySensorIdForPeriodOfTime(@Param("ID") Long sensorId, @Param("TIME") int hours);

}
