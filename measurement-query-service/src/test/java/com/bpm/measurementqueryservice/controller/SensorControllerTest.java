package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import com.bpm.measurementqueryservice.dto.SensorDTO;
import com.bpm.measurementqueryservice.exception.GlobalHttpErrorHandler;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByIdException;
import com.bpm.measurementqueryservice.mapper.MeasurementMapper;
import com.bpm.measurementqueryservice.mapper.SensorMapper;
import com.bpm.measurementqueryservice.service.MeasurementService;
import com.bpm.measurementqueryservice.service.SensorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SensorController.class)
@Import(GlobalHttpErrorHandler.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    @MockitoBean
    private SensorMapper sensorMapper;

    @MockitoBean
    private MeasurementService measurementService;

    @MockitoBean
    private MeasurementMapper measurementMapper;

    private Sensor createSensor() throws Exception {
        Constructor<Sensor> constructor = Sensor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Sensor sensor = constructor.newInstance();

        setField(sensor, "id", 1L);
        setField(sensor, "sensorId", "TEST-001");
        setField(sensor, "sensorType", "DHT22");
        setField(sensor, "location", "kitchen");
        setField(sensor, "lastSeen", LocalDateTime.of(2024, 1, 1, 12, 0));
        setField(sensor, "isOnline", true);
        setField(sensor, "roomDataId", 1L);

        return sensor;
    }

    private Measurement createMeasurement() throws Exception {
        Constructor<Measurement> constructor = Measurement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Measurement measurement = constructor.newInstance();

        setField(measurement, "id", 1L);
        setField(measurement, "sensorId", 1L);
        setField(measurement, "temperature", new BigDecimal("22.50"));
        setField(measurement, "humidity", new BigDecimal("45.00"));
        setField(measurement, "dewPoint", new BigDecimal("10.50"));
        setField(measurement, "timestamp", LocalDateTime.of(2024, 1, 1, 12, 0));

        return measurement;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldGetAllSensors() throws Exception {
        Sensor sensor = createSensor();
        SensorDTO sensorDTO = SensorDTO.builder()
                .id(1L)
                .sensorId("TEST-001")
                .location("kitchen")
                .sensorType("DHT22")
                .lastSeen("2024-01-01T12:00:00")
                .isOnline(true)
                .build();

        when(sensorService.getAllSensors()).thenReturn(List.of(sensor));
        when(sensorMapper.mapToSensorDTOList(List.of(sensor))).thenReturn(List.of(sensorDTO));

        mockMvc.perform(get("/api/v1/sensors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetSensorById() throws Exception {
        Sensor sensor = createSensor();
        SensorDTO sensorDTO = SensorDTO.builder()
                .id(1L)
                .sensorId("TEST-001")
                .location("kitchen")
                .sensorType("DHT22")
                .lastSeen("2024-01-01T12:00:00")
                .isOnline(true)
                .build();

        when(sensorService.getSensorById(1L)).thenReturn(sensor);
        when(sensorMapper.mapToSensorDTO(sensor)).thenReturn(sensorDTO);

        mockMvc.perform(get("/api/v1/sensors/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldThrowExceptionWhenSensorNotFound() throws Exception {
        when(sensorService.getSensorById(anyLong())).thenThrow(new SensorNotFoundByIdException(1L));

        mockMvc.perform(get("/api/v1/sensors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetSensorMeasurements() throws Exception {
        Measurement measurement = createMeasurement();
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .id("1")
                .sensorId("TEST-001")
                .temperature(22.5)
                .humidity(45.0)
                .timestamp("2024-01-01T12:00:00")
                .build();

        when(measurementService.getMeasurementsBySensorIdForPeriodOfTime(1L, 24))
                .thenReturn(List.of(measurement));
        when(measurementMapper.mapToMeasurementDTOList(List.of(measurement)))
                .thenReturn(List.of(measurementDTO));

        mockMvc.perform(get("/api/v1/sensors/1/measurements")
                        .param("hours", "24"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldUseDefaultHoursParameter() throws Exception {
        when(measurementService.getMeasurementsBySensorIdForPeriodOfTime(1L, 24))
                .thenReturn(List.of());
        when(measurementMapper.mapToMeasurementDTOList(List.of()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/sensors/1/measurements"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidHoursParameter() throws Exception {
        mockMvc.perform(get("/api/v1/sensors/1/measurements")
                        .param("hours", "1000"))
                .andExpect(status().isBadRequest());
    }
}
