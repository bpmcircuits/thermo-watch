package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.BME280Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BME280ProcessingStrategy implements SensorProcessingStrategy<BME280Data> {
    private static final Logger logger = LoggerFactory.getLogger(BME280ProcessingStrategy.class);

    private final SensorMeasurementService sensorMeasurementService;

    @Override
    public String getSensorType() {
        return "BME280";
    }

    @Override
    public Class<BME280Data> getDataClass() {
        return BME280Data.class;
    }

    @Override
    public void processSensorData(BME280Data data, SensorMessage message) {
        logger.info("Got data from sensor: {}, location: {}", message.getSensorId(), message.getLocation());
        sensorMeasurementService.sendMeasurement(message, data);
    }


}
