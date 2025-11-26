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
    private static final String AVAILABILITY_TYPE = "AVAILABILITY";

    private final Map<String, SensorProcessingStrategy<?>> strategies;
    private final ObjectMapper objectMapper;

    public SensorProcessingContext(List<SensorProcessingStrategy<?>> strategyList, ObjectMapper objectMapper) {
        this.strategies = new HashMap<>();
        this.objectMapper = objectMapper;

        for (SensorProcessingStrategy<?> strategy : strategyList) {
            strategies.put(strategy.getSensorType(), strategy);
        }
    }

    public void processMessage(SensorMessage message) {
        if (message.getAvailability() != null) {
            processAvailability(message);
        } else {
            processMeasurement(message);
        }
    }

    private void processMeasurement(SensorMessage message) {
        message.getSensorData().forEach((sensorType, rawData) -> {
            SensorProcessingStrategy<?> strategy = strategies.get(sensorType);

            if (strategy != null) {
                try {
                    processTypedSensorData(strategy, rawData, message);
                } catch (Exception e) {
                    logger.error("Error processing sensor data for type: {}", sensorType, e);
                }
            } else {
                logger.warn("No strategy found for sensor type: {}", sensorType);
            }
        });
    }

    private void processAvailability(SensorMessage message) {
        SensorProcessingStrategy<?> availabilityStrategy = strategies.get(AVAILABILITY_TYPE);
        if (availabilityStrategy != null) {
            @SuppressWarnings("unchecked")
            SensorProcessingStrategy<String> typedStrategy =
                    (SensorProcessingStrategy<String>) availabilityStrategy;

            typedStrategy.processSensorData(message.getAvailability(), message);
        } else {
            logger.warn("No availability strategy registered under type: {}", AVAILABILITY_TYPE);
        }
    }

    private <T> void processTypedSensorData(
            SensorProcessingStrategy<T> strategy,
            Object rawData,
            SensorMessage message
    ) {
        Class<T> dataClass = strategy.getDataClass();
        T typedData = objectMapper.convertValue(rawData, dataClass);
        strategy.processSensorData(typedData, message);
    }
}
