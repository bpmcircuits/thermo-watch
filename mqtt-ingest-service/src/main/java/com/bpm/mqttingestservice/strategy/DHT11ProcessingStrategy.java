package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DHT11ProcessingStrategy implements SensorProcessingStrategy<DHT11Data> {

    private static final Logger logger = LoggerFactory.getLogger(DHT11ProcessingStrategy.class);

    private final SensorMeasurementService sensorMeasurementService;

    @Override
    public String getSensorType() {
        return "DHT11";
    }

    @Override
    public Class<DHT11Data> getDataClass() {
        return DHT11Data.class;
    }

    @Override
    public void processSensorData(DHT11Data data, SensorMessage message) {

        String sensorTopic = message.getSensorTopic();
        logger.info("Got data from sensor topic: {}", sensorTopic);
        sensorMeasurementService.send(sensorTopic, data);

    }
}
