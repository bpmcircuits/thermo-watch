package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import com.bpm.measurementqueryservice.exception.GlobalHttpErrorHandler;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.mapper.MeasurementMapper;
import com.bpm.measurementqueryservice.service.MeasurementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LocationController.class)
@Import(GlobalHttpErrorHandler.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeasurementService measurementService;

    @MockitoBean
    private MeasurementMapper measurementMapper;

    @Test
    void shouldGetMeasurementsByLocation() throws Exception {
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .id("1")
                .sensorId("TEST-001")
                .temperature(22.5)
                .humidity(45.0)
                .timestamp("2024-01-01T12:00:00")
                .build();

        when(measurementService.getMeasurementsByLocation("kitchen", 24))
                .thenReturn(List.of());
        when(measurementMapper.mapToMeasurementDTOList(List.of()))
                .thenReturn(List.of(measurementDTO));

        mockMvc.perform(get("/api/v1/locations/kitchen/measurements")
                        .param("hours", "24"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldThrowExceptionWhenLocationNotFound() throws Exception {
        when(measurementService.getMeasurementsByLocation("unknown", 24))
                .thenThrow(new SensorNotFoundByLocationException("unknown"));

        mockMvc.perform(get("/api/v1/locations/unknown/measurements")
                        .param("hours", "24"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidHoursParameter() throws Exception {
        mockMvc.perform(get("/api/v1/locations/kitchen/measurements")
                        .param("hours", "1000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNegativeHoursParameter() throws Exception {
        mockMvc.perform(get("/api/v1/locations/kitchen/measurements")
                        .param("hours", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListWhenNoMeasurements() throws Exception {
        when(measurementService.getMeasurementsByLocation("kitchen", 24))
                .thenReturn(List.of());
        when(measurementMapper.mapToMeasurementDTOList(List.of()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/locations/kitchen/measurements")
                        .param("hours", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
