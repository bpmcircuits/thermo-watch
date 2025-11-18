package com.bpm.measurementqueryservice.controller;

import com.bpm.measurementqueryservice.dto.RoomDataDTO;
import com.bpm.measurementqueryservice.mapper.RoomDataMapper;
import com.bpm.measurementqueryservice.service.RoomDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    private final RoomDataService roomDataService;
    private final RoomDataMapper roomDataMapper;

    @GetMapping
    public ResponseEntity<List<RoomDataDTO>> getRoomsDataInformation() {
        return ResponseEntity.ok(roomDataMapper.mapToRoomDataDTOList(roomDataService.getRoomsDataInformation()));
    }
}
