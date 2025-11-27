package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.measurementstorageservice.service.AvailabilityStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class AvailabilityStorageListenerTest {

    @Mock
    private AvailabilityStorageService availabilityStorageService;

    private AvailabilityStorageListener listener;

    @BeforeEach
    void setUp() {
        listener = new AvailabilityStorageListener(availabilityStorageService);
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
        verify(availabilityStorageService).storeAvailability(event);
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
        verify(availabilityStorageService).storeAvailability(event);
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
                .when(availabilityStorageService).storeAvailability(any());

        // When & Then
        assertThrows(RuntimeException.class, () -> listener.handleAvailability(event));
        verify(availabilityStorageService).storeAvailability(event);
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
        verify(availabilityStorageService).storeAvailability(event1);
        verify(availabilityStorageService).storeAvailability(event2);
        verify(availabilityStorageService, times(2)).storeAvailability(any());
    }

}