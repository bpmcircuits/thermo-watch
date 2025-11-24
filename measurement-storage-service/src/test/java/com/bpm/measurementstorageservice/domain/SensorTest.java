package com.bpm.measurementstorageservice.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SensorTest {

    @Test
    void shouldCreateSensorWithBuilder() {
        // Given
        String sensorId = "DHT11-001";
        String sensorType = "DHT11";
        String location = "Living Room";
        LocalDateTime lastSeen = LocalDateTime.now();
        Boolean isOnline = true;

        // When
        Sensor sensor = Sensor.builder()
                .sensorId(sensorId)
                .sensorType(sensorType)
                .location(location)
                .lastSeen(lastSeen)
                .isOnline(isOnline)
                .build();

        // Then
        assertNotNull(sensor);
        assertEquals(sensorId, sensor.getSensorId());
        assertEquals(sensorType, sensor.getSensorType());
        assertEquals(location, sensor.getLocation());
        assertEquals(lastSeen, sensor.getLastSeen());
        assertEquals(isOnline, sensor.getIsOnline());
    }

    @Test
    void shouldSetRoomData() {
        // Given
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();
        RoomData roomData = RoomData.builder()
                .location("Kitchen")
                .sensors(new ArrayList<>())
                .build();

        // When
        sensor.setRoomData(roomData);

        // Then
        assertEquals(roomData, sensor.getRoomData());
        assertTrue(roomData.getSensors().contains(sensor));
    }

    @Test
    void shouldNotAddDuplicateSensorToRoomData() {
        // Given
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();
        RoomData roomData = RoomData.builder()
                .location("Kitchen")
                .sensors(new ArrayList<>())
                .build();
        sensor.setRoomData(roomData);

        // When
        sensor.setRoomData(roomData);

        // Then
        assertEquals(1, roomData.getSensors().size());
    }

    @Test
    void shouldHandleNullRoomData() {
        // Given
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();

        // When
        sensor.setRoomData(null);

        // Then
        assertNull(sensor.getRoomData());
    }

    @Test
    void shouldHandleNullSensorsListWhenSettingRoomData() {
        // Given
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();
        RoomData roomData = RoomData.builder()
                .location("Kitchen")
                .build();

        // When
        sensor.setRoomData(roomData);

        // Then
        assertEquals(roomData, sensor.getRoomData());
        assertNull(roomData.getSensors());
    }

    @Test
    void shouldManageBidirectionalRelationshipWithMeasurements() {
        // Given
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .measurements(new ArrayList<>())
                .build();
        Measurement measurement = Measurement.builder().build();

        // When
        measurement.attachTo(sensor);

        // Then
        assertEquals(sensor, measurement.getSensor());
        assertTrue(sensor.getMeasurements().contains(measurement));
    }
}
