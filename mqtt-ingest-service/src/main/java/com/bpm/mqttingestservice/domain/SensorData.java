package com.bpm.mqttingestservice.domain;

import com.bpm.events.dto.SensorMeasurementEvent;

public interface SensorData {
    SensorMeasurementEvent toMeasurementEvent(String topic);
}
