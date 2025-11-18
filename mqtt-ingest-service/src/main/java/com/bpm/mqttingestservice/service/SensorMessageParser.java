package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorMessageParser {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SensorMessageParser.class);

    private final ObjectMapper objectMapper;

    public SensorMessage parse(String topic, String payload) {
        if (isLwtTopic(topic)) {
            return parseAvailabilityMessage(topic, payload);
        }
        return parseSensorMessage(topic, payload);
    }

    private boolean isLwtTopic(String topic) {
        return topic.endsWith("/LWT");
    }

    private SensorMessage parseSensorMessage(String topic, String payload) {
        try {
            String sensorTopic = extractLocationFromSensorTopic(topic);
            SensorMessage message = objectMapper.readValue(payload, SensorMessage.class);
            message.setSensorTopic(sensorTopic);
            return message;
        } catch (Exception e) {
            log.error("Failed to parse sensor message from topic: {}", topic, e);
            throw new RuntimeException("Failed to parse sensor payload", e);
        }
    }

    private SensorMessage parseAvailabilityMessage(String topic, String payload) {
        try {
            String sensorTopic = extractLocationFromSensorTopic(topic);

            SensorMessage message = new SensorMessage();
            message.setSensorTopic(sensorTopic);
            message.setAvailability(payload.trim());

            return message;
        } catch (Exception e) {
            log.error("Failed to parse availability message from topic: {}", topic, e);
            throw new RuntimeException("Failed to parse availability payload", e);
        }
    }

    private String extractLocationFromSensorTopic(String topic) {
        String[] parts = topic.split("/");
        return parts.length > 1 ? parts[1] : topic;
    }

}
