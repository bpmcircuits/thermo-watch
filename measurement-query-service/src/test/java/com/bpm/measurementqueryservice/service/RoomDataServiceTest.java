package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.RoomData;
import com.bpm.measurementqueryservice.repository.RoomDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomDataServiceTest {

    @Mock
    private RoomDataRepository roomDataRepository;

    @InjectMocks
    private RoomDataService roomDataService;

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
    void shouldGetAllRoomsData() throws Exception {
        RoomData roomData = createRoomData();
        when(roomDataRepository.findAll()).thenReturn(List.of(roomData));

        List<RoomData> result = roomDataService.getRoomsDataInformation();

        assertEquals(1, result.size());
        assertEquals("Kitchen", result.get(0).getLocation());
        verify(roomDataRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRoomsData() {
        when(roomDataRepository.findAll()).thenReturn(List.of());

        List<RoomData> result = roomDataService.getRoomsDataInformation();

        assertTrue(result.isEmpty());
        verify(roomDataRepository).findAll();
    }
}
