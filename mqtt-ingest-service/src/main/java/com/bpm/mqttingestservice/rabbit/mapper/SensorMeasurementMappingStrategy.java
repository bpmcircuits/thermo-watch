package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.domain.SensorMessage;

public interface SensorMeasurementMappingStrategy<T extends SensorData> {
    Class<T> getSupportedType();
    SensorMeasurementEvent toMeasurementEvent(SensorMessage message, T data);
}
