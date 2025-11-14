package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;

public interface SensorProcessingStrategy {
    String getSensorType();
    Class<?> getDataClass();
    void processSensorData(Object sensorData, SensorMessage message);
}
