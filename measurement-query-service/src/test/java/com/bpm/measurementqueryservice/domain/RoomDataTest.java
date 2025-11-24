package com.bpm.measurementqueryservice.domain;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RoomDataTest {

    private RoomData createRoomData() throws Exception {
        Constructor<RoomData> constructor = RoomData.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        RoomData roomData = constructor.newInstance();

        setField(roomData, "id", 1L);
        setField(roomData, "location", "Kitchen");
        setField(roomData, "currentTemperature", new BigDecimal("22.50"));
        setField(roomData, "currentHumidity", new BigDecimal("45.00"));
        setField(roomData, "sensorCount", 3);

        return roomData;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldCreateRoomDataWithAllFields() throws Exception {
        RoomData roomData = createRoomData();

        assertEquals(1L, roomData.getId());
        assertEquals("Kitchen", roomData.getLocation());
        assertEquals(new BigDecimal("22.50"), roomData.getCurrentTemperature());
        assertEquals(new BigDecimal("45.00"), roomData.getCurrentHumidity());
        assertEquals(3, roomData.getSensorCount());
    }

    @Test
    void shouldHandleZeroSensorCount() throws Exception {
        RoomData roomData = createRoomData();
        setField(roomData, "sensorCount", 0);

        assertEquals(0, roomData.getSensorCount());
    }
}
