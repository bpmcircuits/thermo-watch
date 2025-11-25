package com.bpm.mqttingestservice.mqtt;

import com.bpm.mqttingestservice.service.MqttIngestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MqttMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(MqttMessageHandler.class);

    private static final String MQTT_TOPIC_HEADER = "mqtt_receivedTopic";

    private final MqttIngestService ingestService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = extractTopic(message);
        if (topic == null || topic.isBlank()) {
            log.warn("Skipping MQTT message – missing or blank topic header '{}'", MQTT_TOPIC_HEADER);
            return;
        }

        message.getPayload();
        String payload = message.getPayload().toString();

        try {
            ingestService.ingest(topic, payload);
        } catch (RuntimeException e) {
            log.error("Error processing MQTT message for topic '{}'", topic, e);
        }
    }

    private String extractTopic(Message<?> message) {
        Object headerValue = message.getHeaders().get(MQTT_TOPIC_HEADER);
        return headerValue instanceof String ? (String) headerValue : null;
    }
}
