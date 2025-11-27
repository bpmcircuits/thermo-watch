package com.bpm.measurementstorageservice.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.measurementstorageservice.domain.Sensor;
import com.bpm.measurementstorageservice.repository.MeasurementStorageRepository;
import com.bpm.measurementstorageservice.repository.RoomDataStorageRepository;
import com.bpm.measurementstorageservice.repository.SensorStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityStorageServiceTest {

    @Mock
    private MeasurementStorageRepository measurementRepo;

    @Mock
    private SensorStorageRepository sensorRepo;

    @Mock
    private RoomDataStorageRepository roomRepo;

    @InjectMocks
    private AvailabilityStorageService service;

    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
    }

    @Test
    void shouldStoreAvailabilityOnlineForExistingSensor() {
        // Given
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("online")
                .timestamp(testTimestamp)
                .build();

        Sensor existingSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .location("Living Room")
                .isOnline(false)
                .lastSeen(testTimestamp.minusMinutes(10))
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(existingSensor));

        // When
        service.storeAvailability(event);

        // Then
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertTrue(capturedSensor.getIsOnline());
        assertEquals(testTimestamp, capturedSensor.getLastSeen());
    }

    @Test
    void shouldStoreAvailabilityOfflineForExistingSensor() {
        // Given
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("offline")
                .timestamp(testTimestamp)
                .build();

        Sensor existingSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .location("Living Room")
                .isOnline(true)
                .lastSeen(testTimestamp.minusMinutes(5))
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(existingSensor));

        // When
        service.storeAvailability(event);

        // Then
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertFalse(capturedSensor.getIsOnline());
        assertEquals(testTimestamp, capturedSensor.getLastSeen());
    }

    @Test
    void shouldHandleAvailabilityStatusCaseInsensitive() {
        // Given - uppercase ONLINE
        SensorAvailabilityEvent onlineEvent = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("ONLINE")
                .timestamp(testTimestamp)
                .build();

        Sensor sensor1 = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .location("Living Room")
                .isOnline(false)
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(sensor1));

        // When
        service.storeAvailability(onlineEvent);

        // Then
        ArgumentCaptor<Sensor> captor1 = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(captor1.capture());
        assertTrue(captor1.getValue().getIsOnline());

        // Given - lowercase offline
        reset(sensorRepo);
        SensorAvailabilityEvent offlineEvent = SensorAvailabilityEvent.builder()
                .sensorId("DHT11-001")
                .status("offline")
                .timestamp(testTimestamp)
                .build();

        Sensor sensor2 = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .location("Living Room")
                .isOnline(true)
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(sensor2));

        // When
        service.storeAvailability(offlineEvent);

        // Then
        ArgumentCaptor<Sensor> captor2 = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(captor2.capture());
        assertFalse(captor2.getValue().getIsOnline());
    }

}