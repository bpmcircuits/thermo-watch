package com.bpm.measurementstorageservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomDataTest {

    @Test
    void shouldCreateRoomDataWithBuilder() {
        // Given
        String location = "Living Room";
        BigDecimal currentTemperature = new BigDecimal("22.50");
        BigDecimal currentHumidity = new BigDecimal("55.00");
        Integer sensorCount = 3;

        // When
        RoomData roomData = RoomData.builder()
                .location(location)
                .currentTemperature(currentTemperature)
                .currentHumidity(currentHumidity)
                .sensorCount(sensorCount)
                .build();

        // Then
        assertNotNull(roomData);
        assertEquals(location, roomData.getLocation());
        assertEquals(currentTemperature, roomData.getCurrentTemperature());
        assertEquals(currentHumidity, roomData.getCurrentHumidity());
        assertEquals(sensorCount, roomData.getSensorCount());
    }

    @Test
    void shouldManageBidirectionalRelationshipWithSensors() {
        // Given
        RoomData roomData = RoomData.builder()
                .location("Kitchen")
                .sensors(new ArrayList<>())
                .build();
        Sensor sensor = Sensor.builder()
                .sensorId("sensor-1")
                .build();

        // When
        sensor.setRoomData(roomData);

        // Then
        assertEquals(roomData, sensor.getRoomData());
        assertTrue(roomData.getSensors().contains(sensor));
    }

    @Test
    void shouldHandleMultipleSensors() {
        // Given
        RoomData roomData = RoomData.builder()
                .location("Bedroom")
                .sensors(new ArrayList<>())
                .build();
        Sensor sensor1 = Sensor.builder().sensorId("sensor-1").build();
        Sensor sensor2 = Sensor.builder().sensorId("sensor-2").build();

        // When
        sensor1.setRoomData(roomData);
        sensor2.setRoomData(roomData);

        // Then
        assertEquals(2, roomData.getSensors().size());
        assertTrue(roomData.getSensors().contains(sensor1));
        assertTrue(roomData.getSensors().contains(sensor2));
    }

    @Test
    void shouldSetAllProperties() {
        // Given
        RoomData roomData = new RoomData();

        // When
        roomData.setLocation("Office");
        roomData.setCurrentTemperature(new BigDecimal("23.00"));
        roomData.setCurrentHumidity(new BigDecimal("50.00"));
        roomData.setSensorCount(2);

        // Then
        assertEquals("Office", roomData.getLocation());
        assertEquals(new BigDecimal("23.00"), roomData.getCurrentTemperature());
        assertEquals(new BigDecimal("50.00"), roomData.getCurrentHumidity());
        assertEquals(2, roomData.getSensorCount());
    }

    @Test
    void shouldHandleNullValues() {
        // Given & When
        RoomData roomData = RoomData.builder()
                .location("Garage")
                .build();

        // Then
        assertNull(roomData.getCurrentTemperature());
        assertNull(roomData.getCurrentHumidity());
        assertNull(roomData.getSensorCount());
        assertNull(roomData.getSensors());
    }
}
