package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import com.bpm.measurementqueryservice.exception.MeasurementNotFoundBySensorIdException;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.mapper.MeasurementMapper;
import com.bpm.measurementqueryservice.service.MeasurementService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LocationController {

    private final MeasurementService measurementService;
    private final MeasurementMapper measurementMapper;


    @GetMapping("/{location}/measurements")
    public ResponseEntity<List<MeasurementDTO>> getLocations(@PathVariable String location, @PathParam("hours") int hours)
            throws SensorNotFoundByLocationException, MeasurementNotFoundBySensorIdException {
        return ResponseEntity.ok(measurementMapper.mapToMeasurementDTOList(
                measurementService.getMeasurementsByLocation(location, hours)));
    }
}
