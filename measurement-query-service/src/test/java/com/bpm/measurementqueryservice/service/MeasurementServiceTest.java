package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.repository.MeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorService sensorService;

    @InjectMocks
    private MeasurementService measurementService;

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

    private Sensor createSensor() throws Exception {
        Constructor<Sensor> constructor = Sensor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Sensor sensor = constructor.newInstance();

        setField(sensor, "id", 1L);
        setField(sensor, "sensorId", "TEST-001");
        setField(sensor, "location", "Kitchen");

        return sensor;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldGetMeasurementsBySensorIdForPeriodOfTime() throws Exception {
        Measurement measurement = createMeasurement();
        when(measurementRepository.findMeasurementsBySensorFkForPeriodOfTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(measurement));

        List<Measurement> result = measurementService.getMeasurementsBySensorIdForPeriodOfTime(1L, 24);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("22.50"), result.get(0).getTemperature());

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(measurementRepository).findMeasurementsBySensorFkForPeriodOfTime(eq(1L), captor.capture());
        assertTrue(captor.getValue().isBefore(LocalDateTime.now()));
    }

    @Test
    void shouldReturnEmptyListWhenNoMeasurements() {
        when(measurementRepository.findMeasurementsBySensorFkForPeriodOfTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<Measurement> result = measurementService.getMeasurementsBySensorIdForPeriodOfTime(1L, 24);

        assertTrue(result.isEmpty());
        verify(measurementRepository).findMeasurementsBySensorFkForPeriodOfTime(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void shouldGetMeasurementsByLocation() throws Exception {
        Sensor sensor = createSensor();
        Measurement measurement = createMeasurement();

        when(sensorService.getSensorByLocation("Kitchen")).thenReturn(sensor);
        when(measurementRepository.findMeasurementsBySensorFkForPeriodOfTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(measurement));

        List<Measurement> result = measurementService.getMeasurementsByLocation("Kitchen", 24);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("22.50"), result.get(0).getTemperature());
        verify(sensorService).getSensorByLocation("Kitchen");
        verify(measurementRepository).findMeasurementsBySensorFkForPeriodOfTime(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void shouldThrowExceptionWhenSensorNotFoundByLocation() throws Exception {
        when(sensorService.getSensorByLocation("Unknown"))
                .thenThrow(new SensorNotFoundByLocationException("Unknown"));

        assertThrows(SensorNotFoundByLocationException.class,
                () -> measurementService.getMeasurementsByLocation("Unknown", 24));
        verify(sensorService).getSensorByLocation("Unknown");
    }
}
