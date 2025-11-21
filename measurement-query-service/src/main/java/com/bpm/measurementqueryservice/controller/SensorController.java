package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import com.bpm.measurementqueryservice.dto.SensorDTO;
import com.bpm.measurementqueryservice.exception.MeasurementNotFoundBySensorIdException;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByIdException;
import com.bpm.measurementqueryservice.mapper.MeasurementMapper;
import com.bpm.measurementqueryservice.mapper.SensorMapper;
import com.bpm.measurementqueryservice.service.MeasurementService;
import com.bpm.measurementqueryservice.service.SensorService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SensorController {

    private final SensorService sensorService;
    private final SensorMapper sensorMapper;

    private final MeasurementService measurementService;
    private final MeasurementMapper measurementMapper;

    @GetMapping
    public ResponseEntity<List<SensorDTO>> getSensors() {
        return ResponseEntity.ok(sensorMapper.mapToSensorDTOList(sensorService.getAllSensors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long id) throws SensorNotFoundByIdException {
        return ResponseEntity.ok(sensorMapper.mapToSensorDTO(sensorService.getSensorById(id)));
    }

    @GetMapping("/{id}/measurements")
    public ResponseEntity<List<MeasurementDTO>> getSensorMeasurementsById(
            @PathVariable Long id,
            @RequestParam(value = "hours", defaultValue = "24") @Min(0) @Max(720) Integer hours) {
        List<Measurement> measurements = measurementService.getMeasurementsBySensorIdForPeriodOfTime(id, hours);
        return ResponseEntity.ok(measurementMapper.mapToMeasurementDTOList(measurements));
    }
}
