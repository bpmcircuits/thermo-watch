package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.RoomData;
import com.bpm.measurementqueryservice.dto.RoomDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomDataMapperTest {

    private RoomDataMapper roomDataMapper;

    @BeforeEach
    void setUp() {
        roomDataMapper = new RoomDataMapper();
    }

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
    void shouldMapRoomDataToRoomDataDTO() throws Exception {
        RoomData roomData = createRoomData();

        RoomDataDTO result = roomDataMapper.mapToRoomDataDTO(roomData);

        assertEquals("Kitchen", result.location());
        assertEquals(22.50, result.currentTemperature());
        assertEquals(45.00, result.currentHumidity());
        assertEquals(3, result.sensorCount());
    }

    @Test
    void shouldHandleNullTemperature() throws Exception {
        RoomData roomData = createRoomData();
        setField(roomData, "currentTemperature", null);

        RoomDataDTO result = roomDataMapper.mapToRoomDataDTO(roomData);

        assertNull(result.currentTemperature());
    }

    @Test
    void shouldHandleNullHumidity() throws Exception {
        RoomData roomData = createRoomData();
        setField(roomData, "currentHumidity", null);

        RoomDataDTO result = roomDataMapper.mapToRoomDataDTO(roomData);

        assertNull(result.currentHumidity());
    }

    @Test
    void shouldMapRoomDataListToRoomDataDTOList() throws Exception {
        RoomData roomData1 = createRoomData();
        RoomData roomData2 = createRoomData();
        setField(roomData2, "id", 2L);
        setField(roomData2, "location", "Bedroom");

        List<RoomDataDTO> result = roomDataMapper.mapToRoomDataDTOList(List.of(roomData1, roomData2));

        assertEquals(2, result.size());
        assertEquals("Kitchen", result.get(0).location());
        assertEquals("Bedroom", result.get(1).location());
    }

    @Test
    void shouldReturnEmptyListWhenMappingEmptyList() {
        List<RoomDataDTO> result = roomDataMapper.mapToRoomDataDTOList(List.of());

        assertTrue(result.isEmpty());
    }
}
