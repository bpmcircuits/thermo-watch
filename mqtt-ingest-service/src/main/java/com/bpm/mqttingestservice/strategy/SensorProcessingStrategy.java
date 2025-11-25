package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.SensorMessage;

public interface SensorProcessingStrategy<T> {
    String getSensorType();
    Class<T> getDataClass();
    void processSensorData(T sensorData, SensorMessage message);
}
