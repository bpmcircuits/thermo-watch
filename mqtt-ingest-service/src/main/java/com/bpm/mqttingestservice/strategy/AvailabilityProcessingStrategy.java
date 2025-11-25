package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvailabilityProcessingStrategy implements SensorProcessingStrategy<String> {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityProcessingStrategy.class);
    private final SensorMeasurementService sensorMeasurementService;

    @Override
    public String getSensorType() {
        return "AVAILABILITY";
    }

    @Override
    public Class<String> getDataClass() {
        return String.class;
    }

    @Override
    public void processSensorData(String data, SensorMessage message) {
        String availability = message.getAvailability();
        String sensorTopic = message.getSensorTopic();

        logger.info("Device {} is now: {}", sensorTopic, availability);

        sensorMeasurementService.sendAvailability(sensorTopic, availability);
    }
}
