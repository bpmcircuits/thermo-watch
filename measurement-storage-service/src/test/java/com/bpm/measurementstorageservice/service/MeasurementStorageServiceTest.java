package com.bpm.measurementstorageservice.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.domain.Measurement;
import com.bpm.measurementstorageservice.domain.RoomData;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementStorageServiceTest {

    @Mock
    private MeasurementStorageRepository measurementRepo;

    @Mock
    private SensorStorageRepository sensorRepo;

    @Mock
    private RoomDataStorageRepository roomRepo;

    @InjectMocks
    private MeasurementStorageService service;

    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
    }

    @Test
    void shouldStoreMeasurementWithNewRoomAndSensor() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.5"))
                .humidity(new BigDecimal("60.0"))
                .dewPoint(new BigDecimal("17.3"))
                .timestamp(testTimestamp)
                .build();

        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.empty());
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.empty());

        RoomData savedRoom = RoomData.builder()
                .id(1L)
                .location("Living Room")
                .currentTemperature(new BigDecimal("25.5"))
                .currentHumidity(new BigDecimal("60.0"))
                .build();
        when(roomRepo.save(any(RoomData.class))).thenReturn(savedRoom);

        Sensor savedSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .build();
        when(sensorRepo.save(any(Sensor.class))).thenReturn(savedSensor);

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<RoomData> roomCaptor = ArgumentCaptor.forClass(RoomData.class);
        verify(roomRepo).save(roomCaptor.capture());
        RoomData capturedRoom = roomCaptor.getValue();
        assertEquals("Living Room", capturedRoom.getLocation());
        assertEquals(new BigDecimal("25.5"), capturedRoom.getCurrentTemperature());
        assertEquals(new BigDecimal("60.0"), capturedRoom.getCurrentHumidity());

        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertEquals("DHT11-001", capturedSensor.getSensorId());
        assertEquals("DHT11", capturedSensor.getSensorType());
        assertEquals("Living Room", capturedSensor.getLocation());
        assertEquals(testTimestamp, capturedSensor.getLastSeen());
        assertTrue(capturedSensor.getIsOnline());
        assertNotNull(capturedSensor.getRoomData());

        ArgumentCaptor<Measurement> measurementCaptor = ArgumentCaptor.forClass(Measurement.class);
        verify(measurementRepo).save(measurementCaptor.capture());
        Measurement capturedMeasurement = measurementCaptor.getValue();
        assertEquals(new BigDecimal("25.5"), capturedMeasurement.getTemperature());
        assertEquals(new BigDecimal("60.0"), capturedMeasurement.getHumidity());
        assertEquals(new BigDecimal("17.3"), capturedMeasurement.getDewPoint());
        assertEquals(testTimestamp, capturedMeasurement.getTimestamp());
        assertNotNull(capturedMeasurement.getSensor());
    }

    @Test
    void shouldStoreMeasurementWithExistingRoomAndSensor() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("26.0"))
                .humidity(new BigDecimal("65.0"))
                .dewPoint(new BigDecimal("18.0"))
                .timestamp(testTimestamp)
                .build();

        RoomData existingRoom = RoomData.builder()
                .id(1L)
                .location("Living Room")
                .currentTemperature(new BigDecimal("25.0"))
                .currentHumidity(new BigDecimal("60.0"))
                .build();
        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.of(existingRoom));

        Sensor existingSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .sensorType("DHT11")
                .location("Living Room")
                .lastSeen(testTimestamp.minusMinutes(5))
                .isOnline(false)
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(existingSensor));

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<RoomData> roomCaptor = ArgumentCaptor.forClass(RoomData.class);
        verify(roomRepo).save(roomCaptor.capture());
        RoomData capturedRoom = roomCaptor.getValue();
        assertEquals(1L, capturedRoom.getId());
        assertEquals(new BigDecimal("26.0"), capturedRoom.getCurrentTemperature());
        assertEquals(new BigDecimal("65.0"), capturedRoom.getCurrentHumidity());

        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertEquals(1L, capturedSensor.getId());
        assertEquals(testTimestamp, capturedSensor.getLastSeen());
        assertTrue(capturedSensor.getIsOnline());

        verify(measurementRepo).save(any(Measurement.class));
    }

    @Test
    void shouldStoreMeasurementWithNullHumidityAndDewPoint() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DS18B20")
                .sensorId("DS18B20-001")
                .location("Bedroom")
                .temperature(new BigDecimal("22.3"))
                .humidity(null)
                .dewPoint(null)
                .timestamp(testTimestamp)
                .build();

        when(roomRepo.findByLocation("Bedroom")).thenReturn(Optional.empty());
        when(sensorRepo.findBySensorId("DS18B20-001")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<RoomData> roomCaptor = ArgumentCaptor.forClass(RoomData.class);
        verify(roomRepo).save(roomCaptor.capture());
        RoomData capturedRoom = roomCaptor.getValue();
        assertEquals(new BigDecimal("22.3"), capturedRoom.getCurrentTemperature());
        assertNull(capturedRoom.getCurrentHumidity());

        ArgumentCaptor<Measurement> measurementCaptor = ArgumentCaptor.forClass(Measurement.class);
        verify(measurementRepo).save(measurementCaptor.capture());
        Measurement capturedMeasurement = measurementCaptor.getValue();
        assertEquals(new BigDecimal("22.3"), capturedMeasurement.getTemperature());
        assertNull(capturedMeasurement.getHumidity());
        assertNull(capturedMeasurement.getDewPoint());
    }

    @Test
    void shouldUpdateSensorLocationWhenChanged() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Kitchen")
                .temperature(new BigDecimal("24.0"))
                .humidity(new BigDecimal("55.0"))
                .dewPoint(new BigDecimal("15.0"))
                .timestamp(testTimestamp)
                .build();

        Sensor existingSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .sensorType("DHT11")
                .location("Living Room")
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(existingSensor));
        when(roomRepo.findByLocation("Kitchen")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertEquals("Kitchen", capturedSensor.getLocation());
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

    @Test
    void shouldUpdateRoomDataWithEachMeasurement() {
        // Given - First measurement
        SensorMeasurementEvent event1 = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.0"))
                .humidity(new BigDecimal("60.0"))
                .timestamp(testTimestamp)
                .build();

        RoomData existingRoom = RoomData.builder()
                .id(1L)
                .location("Living Room")
                .currentTemperature(new BigDecimal("24.0"))
                .currentHumidity(new BigDecimal("55.0"))
                .build();
        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.of(existingRoom));
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event1);

        // Then
        ArgumentCaptor<RoomData> roomCaptor = ArgumentCaptor.forClass(RoomData.class);
        verify(roomRepo).save(roomCaptor.capture());
        RoomData updatedRoom = roomCaptor.getValue();
        assertEquals(new BigDecimal("25.0"), updatedRoom.getCurrentTemperature());
        assertEquals(new BigDecimal("60.0"), updatedRoom.getCurrentHumidity());
    }

    @Test
    void shouldSetSensorOnlineWhenStoringMeasurement() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.0"))
                .humidity(new BigDecimal("60.0"))
                .timestamp(testTimestamp)
                .build();

        Sensor offlineSensor = Sensor.builder()
                .id(1L)
                .sensorId("DHT11-001")
                .isOnline(false)
                .build();
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.of(offlineSensor));
        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertTrue(capturedSensor.getIsOnline());
        assertEquals(testTimestamp, capturedSensor.getLastSeen());
    }

    @Test
    void shouldLinkSensorToRoomWhenStoringMeasurement() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.0"))
                .timestamp(testTimestamp)
                .build();

        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.empty());
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.empty());

        RoomData savedRoom = RoomData.builder()
                .id(1L)
                .location("Living Room")
                .build();
        when(roomRepo.save(any(RoomData.class))).thenReturn(savedRoom);

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepo).save(sensorCaptor.capture());
        Sensor capturedSensor = sensorCaptor.getValue();
        assertNotNull(capturedSensor.getRoomData());
        assertEquals("Living Room", capturedSensor.getRoomData().getLocation());
    }

    @Test
    void shouldHandleExtremeTemperatureValues() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Test Room")
                .temperature(new BigDecimal("-40.0"))
                .humidity(new BigDecimal("100.0"))
                .dewPoint(new BigDecimal("-40.0"))
                .timestamp(testTimestamp)
                .build();

        when(roomRepo.findByLocation("Test Room")).thenReturn(Optional.empty());
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event);

        // Then
        ArgumentCaptor<Measurement> measurementCaptor = ArgumentCaptor.forClass(Measurement.class);
        verify(measurementRepo).save(measurementCaptor.capture());
        Measurement captured = measurementCaptor.getValue();
        assertEquals(new BigDecimal("-40.0"), captured.getTemperature());
        assertEquals(new BigDecimal("100.0"), captured.getHumidity());
    }

    @Test
    void shouldVerifyTransactionalBehaviorByCheckingAllSaves() {
        // Given
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorType("DHT11")
                .sensorId("DHT11-001")
                .location("Living Room")
                .temperature(new BigDecimal("25.0"))
                .humidity(new BigDecimal("60.0"))
                .dewPoint(new BigDecimal("17.0"))
                .timestamp(testTimestamp)
                .build();

        when(roomRepo.findByLocation("Living Room")).thenReturn(Optional.empty());
        when(sensorRepo.findBySensorId("DHT11-001")).thenReturn(Optional.empty());

        // When
        service.storeMeasurement(event);

        // Then - verify all entities are saved in correct order
        verify(roomRepo).save(any(RoomData.class));
        verify(sensorRepo).save(any(Sensor.class));
        verify(measurementRepo).save(any(Measurement.class));
    }
}
