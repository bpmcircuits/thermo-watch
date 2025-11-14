package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DS18B20ProcessingStrategy implements SensorProcessingStrategy {

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

        System.out.println("Sensor topic: " + message.getSensorTopic());
        System.out.println("Processing DS18B20 data:");
        System.out.println("Id: " + ds18B20Data.getId());
        System.out.println("Temperature: " + ds18B20Data.getTemperature() + message.getTemperatureUnit());

        String sensorTopic = message.getSensorTopic();

        sensorMeasurementService.send(sensorTopic, ds18B20Data);
    }
}
