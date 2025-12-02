package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementMapperTest {

    private MeasurementMapper measurementMapper;

    @BeforeEach
    void setUp() {
        measurementMapper = new MeasurementMapper();
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
    void shouldMapMeasurementToMeasurementDTO() throws Exception {
        Measurement measurement = createMeasurement();

        MeasurementDTO result = measurementMapper.mapToMeasurementDTO(measurement);

        assertEquals("1", result.id());
        assertEquals("1", result.sensorId());
        assertEquals(22.50, result.temperature());
        assertEquals(45.00, result.humidity());
        assertEquals("2024-01-01 12:00:00", result.timestamp());
    }

    @Test
    void shouldHandleNullTemperature() throws Exception {
        Measurement measurement = createMeasurement();
        setField(measurement, "temperature", null);

        MeasurementDTO result = measurementMapper.mapToMeasurementDTO(measurement);

        assertNull(result.temperature());
    }

    @Test
    void shouldHandleNullHumidity() throws Exception {
        Measurement measurement = createMeasurement();
        setField(measurement, "humidity", null);

        MeasurementDTO result = measurementMapper.mapToMeasurementDTO(measurement);

        assertNull(result.humidity());
    }

    @Test
    void shouldMapMeasurementListToMeasurementDTOList() throws Exception {
        Measurement measurement1 = createMeasurement();
        Measurement measurement2 = createMeasurement();
        setField(measurement2, "id", 2L);
        setField(measurement2, "sensorId", 2L);

        List<MeasurementDTO> result = measurementMapper.mapToMeasurementDTOList(List.of(measurement1, measurement2));

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).sensorId());
        assertEquals("2", result.get(1).sensorId());
    }

    @Test
    void shouldReturnEmptyListWhenMappingEmptyList() {
        List<MeasurementDTO> result = measurementMapper.mapToMeasurementDTOList(List.of());

        assertTrue(result.isEmpty());
    }
}
