package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.rabbit.dto.SensorMeasurementEvent;
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
