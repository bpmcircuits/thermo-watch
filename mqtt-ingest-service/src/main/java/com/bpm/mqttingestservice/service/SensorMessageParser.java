package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorMessageParser {

    private final ObjectMapper objectMapper;

    public SensorMessage parse(String topic,String payload) {
        try {
            String sensorTopic = extractSensorTopic(topic);
            SensorMessage message = objectMapper.readValue(payload, SensorMessage.class);
            message.setSensorTopic(sensorTopic);
            return message;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse payload", e);
        }
    }

    private String extractSensorTopic(String topic) {
        String[] parts = topic.split("/");
        return parts.length > 1 ? parts[1] : topic;
    }

}
