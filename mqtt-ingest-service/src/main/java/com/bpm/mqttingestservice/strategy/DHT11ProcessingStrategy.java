package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DHT11ProcessingStrategy implements SensorProcessingStrategy {

    private final SensorMeasurementService sensorMeasurementService;

    @Override
    public String getSensorType() {
        return "DHT11";
    }

    @Override
    public Class<?> getDataClass() {
        return DHT11Data.class;
    }

    @Override
    public void processSensorData(Object data, SensorMessage message) {
        DHT11Data dht11Data = (DHT11Data) data;

        System.out.println("Sensor topic: " + message.getSensorTopic());
        System.out.println("Processing DHT11 data:");
        System.out.println("Temperature: " + dht11Data.getTemperature() + message.getTemperatureUnit());
        System.out.println("Humidity: " + dht11Data.getHumidity() + "%");
        System.out.println("Dew Point: " + dht11Data.getDewPoint() + message.getTemperatureUnit());

        String sensorTopic = message.getSensorTopic();

        sensorMeasurementService.send(sensorTopic, dht11Data);

    }
}
