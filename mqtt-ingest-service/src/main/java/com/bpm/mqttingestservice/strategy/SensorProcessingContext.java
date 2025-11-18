package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorProcessingContext {
    private static final Logger logger = LoggerFactory.getLogger(SensorProcessingContext.class);
    private final Map<String, SensorProcessingStrategy> strategies;
    private final ObjectMapper objectMapper;

    public SensorProcessingContext(List<SensorProcessingStrategy> strategyList, ObjectMapper objectMapper) {
        this.strategies = new HashMap<>();
        this.objectMapper = objectMapper;

        for (SensorProcessingStrategy strategy : strategyList) {
            strategies.put(strategy.getSensorType(), strategy);
        }
    }

    public void processSensorMessage(SensorMessage message) {
        if (message.getAvailability() != null) {
            SensorProcessingStrategy strategy = strategies.get("AVAILABILITY");
            if (strategy != null) {
                strategy.processSensorData(message.getAvailability(), message);
            }
            return;
        }

        message.getSensorData().forEach((sensorType, rawData) -> {
            SensorProcessingStrategy strategy = strategies.get(sensorType);

            if (strategy != null) {
                try {
                    Object typedData = objectMapper.convertValue(rawData, strategy.getDataClass());
                    strategy.processSensorData(typedData, message);
                } catch (Exception e) {
                    logger.error("Error processing sensor data for type: {}", sensorType, e);
                }
            } else {
                logger.warn("No strategy found for sensor type: {}", sensorType);
            }
        });
    }
}
