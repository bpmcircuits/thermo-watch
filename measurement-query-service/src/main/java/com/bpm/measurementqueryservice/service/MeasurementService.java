package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.exception.MeasurementNotFoundBySensorIdException;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorService sensorService;

    public List<Measurement> getMeasurementsBySensorIdForPeriodOfTime(Long sensorFk, int hours)
            throws MeasurementNotFoundBySensorIdException {
        LocalDateTime after = LocalDateTime.now().minusHours(hours);
        return measurementRepository.findMeasurementsBySensorFkForPeriodOfTime(sensorFk, after).orElseThrow(
                () -> new MeasurementNotFoundBySensorIdException(sensorFk));
    }

    public List<Measurement> getMeasurementsByLocation(String location, int hours)
            throws SensorNotFoundByLocationException, MeasurementNotFoundBySensorIdException {
        Sensor sensor = sensorService.getSensorByLocation(location);
        return getMeasurementsBySensorIdForPeriodOfTime(sensor.getId(), hours);
    }
}
