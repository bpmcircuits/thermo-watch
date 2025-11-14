package com.bpm.mqttingestservice.domain;

import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;

public interface SensorData {
    SensorMeasurementEvent toMeasurementEvent(String topic);
}
