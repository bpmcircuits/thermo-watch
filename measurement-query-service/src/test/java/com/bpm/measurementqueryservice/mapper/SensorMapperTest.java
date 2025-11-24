package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.dto.SensorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensorMapperTest {

    private SensorMapper sensorMapper;

    @BeforeEach
    void setUp() {
        sensorMapper = new SensorMapper();
    }

    private Sensor createSensor() throws Exception {
        Constructor<Sensor> constructor = Sensor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Sensor sensor = constructor.newInstance();

        setField(sensor, "id", 1L);
        setField(sensor, "sensorId", "TEST-001");
        setField(sensor, "sensorType", "DHT22");
        setField(sensor, "location", "Kitchen");
        setField(sensor, "lastSeen", "2024-01-01T12:00:00");
        setField(sensor, "isOnline", true);

        return sensor;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldMapSensorToSensorDTO() throws Exception {
        Sensor sensor = createSensor();

        SensorDTO result = sensorMapper.mapToSensorDTO(sensor);

        assertEquals(1L, result.id());
        assertEquals("TEST-001", result.sensorId());
        assertEquals("Kitchen", result.location());
        assertEquals("DHT22", result.sensorType());
        assertEquals("2024-01-01T12:00:00", result.lastSeen());
        assertTrue(result.isOnline());
    }

    @Test
    void shouldMapOfflineSensor() throws Exception {
        Sensor sensor = createSensor();
        setField(sensor, "isOnline", false);

        SensorDTO result = sensorMapper.mapToSensorDTO(sensor);

        assertFalse(result.isOnline());
    }

    @Test
    void shouldMapSensorListToSensorDTOList() throws Exception {
        Sensor sensor1 = createSensor();
        Sensor sensor2 = createSensor();
        setField(sensor2, "id", 2L);
        setField(sensor2, "sensorId", "TEST-002");

        List<SensorDTO> result = sensorMapper.mapToSensorDTOList(List.of(sensor1, sensor2));

        assertEquals(2, result.size());
        assertEquals("TEST-001", result.get(0).sensorId());
        assertEquals("TEST-002", result.get(1).sensorId());
    }

    @Test
    void shouldReturnEmptyListWhenMappingEmptyList() {
        List<SensorDTO> result = sensorMapper.mapToSensorDTOList(List.of());

        assertTrue(result.isEmpty());
    }
}
