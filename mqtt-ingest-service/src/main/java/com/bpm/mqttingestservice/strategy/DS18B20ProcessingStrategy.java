package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DS18B20ProcessingStrategy implements SensorProcessingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DS18B20ProcessingStrategy.class);

    private final SensorMeasurementService sensorMeasurementService;

    @Override
    public String getSensorType() {
        return "DS18B20";
    }

    @Override
    public Class<?> getDataClass() {
        return DS18B20Data.class;
    }

    @Override
    public void processSensorData(Object sensorData, SensorMessage message) {
        DS18B20Data ds18B20Data = (DS18B20Data) sensorData;

        String sensorTopic = message.getSensorTopic();
        logger.info("Got data from sensor topic: {}", sensorTopic);
        sensorMeasurementService.send(sensorTopic, ds18B20Data);
    }
}
