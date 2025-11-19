package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import org.springframework.stereotype.Service;

@Service
public class SensorMeasurementMapper {

    public SensorMeasurementEvent mapToSensorMeasurementEvent(String topic, SensorData sensorData) {
        if (sensorData == null) {
            throw new IllegalArgumentException("sensorData cannot be null");
        }
        return sensorData.toMeasurementEvent(topic);
    }
}
