package com.bpm.measurementqueryservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @GetMapping
    public String getRooms() {
        return "Room 1, Room 2, Room 3";
    }
}
