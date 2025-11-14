package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorProcessingContext {

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
        message.getSensorData().forEach((sensorType, rawData) -> {

            SensorProcessingStrategy strategy = strategies.get(sensorType);

            if (strategy != null) {
                try {
                    Object typedData = objectMapper.convertValue(rawData, strategy.getDataClass());
                    strategy.processSensorData(typedData, message);
                } catch (Exception e) {
                    System.err.println("Error processing sensor data: " + e.getMessage());
                }
            } else {
                System.err.println("No strategy found for sensor type: " + sensorType);
            }
        });
    }
}
