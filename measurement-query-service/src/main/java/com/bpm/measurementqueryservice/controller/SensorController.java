package com.bpm.measurementqueryservice.controller;

import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    @GetMapping
    public List<String> getSensors() {
        return List.of("DHT11", "DS18B20");
    }

    @GetMapping("/{id}")
    public String getSensorById(@PathVariable Long id) {
        return "Sensor with ID: " + id;
    }

    @GetMapping("/{id}/data")
    public String getSensorDataById(@PathVariable Long id, @PathParam("hours") int hours) {
        return "Sensor data with ID: " + id + " for last " + hours + " hours";
    }
}
