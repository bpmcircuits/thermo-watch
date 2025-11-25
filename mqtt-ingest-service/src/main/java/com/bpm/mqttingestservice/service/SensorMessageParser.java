package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.exception.MessageParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorMessageParser {
    private static final Logger log = LoggerFactory.getLogger(SensorMessageParser.class);

    private static final String LWT_SUFFIX = "/LWT";

    private final ObjectMapper objectMapper;

    public SensorMessage parse(String topic, String payload) {
        validateTopic(topic);

        if (isLwtTopic(topic)) {
            return parseAvailabilityMessage(topic, payload);
        }
        return parseSensorMessage(topic, payload);
    }

    private void validateTopic(String topic) {
        if (topic == null) {
            throw new MessageParsingException("MQTT topic must not be null");
        }
    }

    private boolean isLwtTopic(String topic) {
        return topic.endsWith(LWT_SUFFIX);
    }

    private SensorMessage parseSensorMessage(String topic, String payload) {
        try {
            String sensorTopic = extractLocationFromSensorTopic(topic);
            SensorMessage message = objectMapper.readValue(payload, SensorMessage.class);
            message.setSensorTopic(sensorTopic);
            return message;
        } catch (Exception e) {
            log.error("Failed to parse sensor message from topic: {}", topic, e);
            throw new MessageParsingException("Failed to parse sensor payload for topic: " + topic, e);
        }
    }

    private SensorMessage parseAvailabilityMessage(String topic, String payload) {
        try {
            String sensorTopic = extractLocationFromSensorTopic(topic);

            SensorMessage message = new SensorMessage();
            message.setSensorTopic(sensorTopic);
            message.setAvailability(payload == null ? null : payload.trim());

            return message;
        } catch (Exception e) {
            log.error("Failed to parse availability message from topic: {}", topic, e);
            throw new MessageParsingException("Failed to parse availability payload for topic: " + topic, e);
        }
    }

    private String extractLocationFromSensorTopic(String topic) {
        String[] parts = topic.split("/");
        return parts.length > 1 ? parts[1] : topic;
    }

}
