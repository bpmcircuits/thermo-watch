package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.service.MeasurementStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementStorageListenerTest {

    @Mock
    private MeasurementStorageService measurementStorageService;

    private MeasurementStorageListener listener;

    @BeforeEach
    void setUp() {
        listener = new MeasurementStorageListener(measurementStorageService);
    }

    @Test
    void shouldHandleMeasurementEvent() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.5"))
                .humidity(new BigDecimal("60.0"))
                .dewPoint(new BigDecimal("17.3"))
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleMeasurement(event);

        // Then
        verify(measurementStorageService).storeMeasurement(event);
    }

    @Test
    void shouldHandleAvailabilityOnlineEvent() {
        // Given
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .source("DHT11-001")
                .status("online")
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleAvailability(event);

        // Then
        verify(measurementStorageService).storeAvailability(event);
    }

    @Test
    void shouldHandleAvailabilityOfflineEvent() {
        // Given
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .source("DHT11-002")
                .status("offline")
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleAvailability(event);

        // Then
        verify(measurementStorageService).storeAvailability(event);
    }

    @Test
    void shouldHandleMeasurementWithNullHumidity() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DS18B20")
                .sensorId("DS18B20-001")
                .location("Bedroom")
                .temperature(new BigDecimal("22.3"))
                .humidity(null)
                .dewPoint(null)
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleMeasurement(event);

        // Then
        verify(measurementStorageService).storeMeasurement(event);
    }

    @Test
    void shouldHandleMeasurementWithAllFields() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-002")
                .location("Kitchen")
                .temperature(new BigDecimal("24.0"))
                .humidity(new BigDecimal("55.0"))
                .dewPoint(new BigDecimal("15.0"))
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleMeasurement(event);

        // Then
        verify(measurementStorageService).storeMeasurement(event);
    }

    @Test
    void shouldPropagateExceptionWhenServiceThrowsException() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.5"))
                .timestamp(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Database error"))
                .when(measurementStorageService).storeMeasurement(any());

        // When & Then
        assertThrows(RuntimeException.class, () -> listener.handleMeasurement(event));
        verify(measurementStorageService).storeMeasurement(event);
    }

    @Test
    void shouldPropagateExceptionForAvailability() {
        // Given
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .source("DHT11-001")
                .status("online")
                .timestamp(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Database error"))
                .when(measurementStorageService).storeAvailability(any());

        // When & Then
        assertThrows(RuntimeException.class, () -> listener.handleAvailability(event));
        verify(measurementStorageService).storeAvailability(event);
    }

    @Test
    void shouldHandleMultipleMeasurements() {
        // Given
        SensorMeasurementEvent event1 = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.5"))
                .timestamp(LocalDateTime.now())
                .build();

        SensorMeasurementEvent event2 = SensorMeasurementEvent.builder()
                .sensorType("DS18B20")
                .sensorId("DS18B20-001")
                .location("Bedroom")
                .temperature(new BigDecimal("22.3"))
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleMeasurement(event1);
        listener.handleMeasurement(event2);

        // Then
        verify(measurementStorageService).storeMeasurement(event1);
        verify(measurementStorageService).storeMeasurement(event2);
        verify(measurementStorageService, times(2)).storeMeasurement(any());
    }

    @Test
    void shouldHandleMultipleAvailabilityChanges() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        SensorAvailabilityEvent event1 = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("online")
                .timestamp(now)
                .build();

        SensorAvailabilityEvent event2 = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("offline")
                .timestamp(now.plusMinutes(5))
                .build();

        // When
        listener.handleAvailability(event1);
        listener.handleAvailability(event2);

        // Then
        verify(measurementStorageService).storeAvailability(event1);
        verify(measurementStorageService).storeAvailability(event2);
        verify(measurementStorageService, times(2)).storeAvailability(any());
    }

    @Test
    void shouldHandleMeasurementWithPreciseTimestamp() {
        // Given
        LocalDateTime exactTimestamp = LocalDateTime.of(2024, 1, 15, 14, 30, 45, 123456789);
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Test Room")
                .temperature(new BigDecimal("25.0"))
                .humidity(new BigDecimal("60.0"))
                .dewPoint(new BigDecimal("17.0"))
                .timestamp(exactTimestamp)
                .build();

        // When
        listener.handleMeasurement(event);

        // Then
        verify(measurementStorageService).storeMeasurement(event);
    }

    @Test
    void shouldHandleMeasurementWithExtremeValues() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Test Location")
                .temperature(new BigDecimal("-40.0"))
                .humidity(new BigDecimal("100.0"))
                .dewPoint(new BigDecimal("-40.0"))
                .timestamp(LocalDateTime.now())
                .build();

        // When
        listener.handleMeasurement(event);

        // Then
        verify(measurementStorageService).storeMeasurement(event);
    }
}
