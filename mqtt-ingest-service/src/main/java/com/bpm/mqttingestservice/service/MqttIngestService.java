package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.strategy.SensorProcessingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttIngestService {

    private final SensorMessageParser parser;
    private final SensorProcessingContext processingContext;

    public void ingest(String topic, String payload) {
        SensorMessage message = parser.parse(topic, payload);
        processingContext.processMessage(message);
    }
}
