package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByIdException;
import com.bpm.measurementqueryservice.exception.SensorNotFoundByLocationException;
import com.bpm.measurementqueryservice.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorService sensorService;

    private Sensor createSensor() throws Exception {
        Constructor<Sensor> constructor = Sensor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Sensor sensor = constructor.newInstance();

        setField(sensor, "id", 1L);
        setField(sensor, "sensorId", "TEST-001");
        setField(sensor, "sensorType", "DHT22");
        setField(sensor, "location", "Kitchen");
        setField(sensor, "lastSeen", LocalDateTime.of(2024, 1, 1, 12, 0));
        setField(sensor, "isOnline", true);

        return sensor;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldGetAllSensors() throws Exception {
        Sensor sensor = createSensor();
        when(sensorRepository.findAll()).thenReturn(List.of(sensor));

        List<Sensor> result = sensorService.getAllSensors();

        assertEquals(1, result.size());
        assertEquals("TEST-001", result.get(0).getSensorId());
        verify(sensorRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoSensors() {
        when(sensorRepository.findAll()).thenReturn(List.of());

        List<Sensor> result = sensorService.getAllSensors();

        assertTrue(result.isEmpty());
        verify(sensorRepository).findAll();
    }

    @Test
    void shouldGetSensorById() throws Exception {
        Sensor sensor = createSensor();
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));

        Sensor result = sensorService.getSensorById(1L);

        assertEquals(1L, result.getId());
        assertEquals("TEST-001", result.getSensorId());
        verify(sensorRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenSensorNotFoundById() {
        when(sensorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundByIdException.class, () -> sensorService.getSensorById(1L));
        verify(sensorRepository).findById(1L);
    }

    @Test
    void shouldGetSensorByLocation() throws Exception {
        Sensor sensor = createSensor();
        when(sensorRepository.findSensorByLocation("Kitchen")).thenReturn(Optional.of(sensor));

        Sensor result = sensorService.getSensorByLocation("Kitchen");

        assertEquals("Kitchen", result.getLocation());
        assertEquals("TEST-001", result.getSensorId());
        verify(sensorRepository).findSensorByLocation("Kitchen");
    }

    @Test
    void shouldThrowExceptionWhenSensorNotFoundByLocation() {
        when(sensorRepository.findSensorByLocation("Unknown")).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundByLocationException.class,
                () -> sensorService.getSensorByLocation("Unknown"));
        verify(sensorRepository).findSensorByLocation("Unknown");
    }
}
