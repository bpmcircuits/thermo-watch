package com.bpm.mqttingestservice.mqtt;

import com.bpm.mqttingestservice.service.MqttIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MqttMessageHandler {

    private final MqttIngestService ingestService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String payload = message.getPayload().toString();
            String sensorTopic = (Objects.requireNonNull(
                    message.getHeaders().get("mqtt_receivedTopic"))
                    .toString());

            ingestService.ingest(sensorTopic, payload);
        } catch (Exception e) {
            System.err.println("Error processing MQTT message: " + e.getMessage());
        }
    }
}
