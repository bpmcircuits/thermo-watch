package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import com.bpm.measurementqueryservice.dto.SensorDTO;
import com.bpm.measurementqueryservice.mapper.MeasurementMapper;
import com.bpm.measurementqueryservice.mapper.SensorMapper;
import com.bpm.measurementqueryservice.service.MeasurementService;
import com.bpm.measurementqueryservice.service.SensorService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
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
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long id) {
        return sensorService.getSensorById(id)
                .map(sensorMapper::mapToSensorDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/measurements")
    public ResponseEntity<List<MeasurementDTO>> getSensorDataById(@PathVariable Long id, @PathParam("hours") int hours) {
        List<Measurement> measurements = measurementService.getMeasurementsBySensorIdForPeriodOfTime(id, hours);
        return ResponseEntity.ok(measurementMapper.mapToMeasurementDTOList(measurements));
    }
}
