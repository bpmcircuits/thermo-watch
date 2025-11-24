package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.dto.RoomDataDTO;
import com.bpm.measurementqueryservice.mapper.RoomDataMapper;
import com.bpm.measurementqueryservice.service.RoomDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomDataService roomDataService;

    @MockitoBean
    private RoomDataMapper roomDataMapper;

    @Test
    void shouldGetRoomsDataInformation() throws Exception {
        RoomDataDTO roomDataDTO = RoomDataDTO.builder()
                .location("Kitchen")
                .currentTemperature(22.5)
                .currentHumidity(45.0)
                .sensorCount(3)
                .build();

        when(roomDataService.getRoomsDataInformation()).thenReturn(List.of());
        when(roomDataMapper.mapToRoomDataDTOList(List.of())).thenReturn(List.of(roomDataDTO));

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmptyListWhenNoRooms() throws Exception {
        when(roomDataService.getRoomsDataInformation()).thenReturn(List.of());
        when(roomDataMapper.mapToRoomDataDTOList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
