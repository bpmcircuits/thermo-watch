package com.bpm.mqttingestservice.rabbit.service;

import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.rabbit.mapper.SensorMeasurementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorMeasurementService {

    private final RabbitTemplate rabbitTemplate;
    private final SensorMeasurementMapper sensorMeasurementMapper;

    public void send(String topic, SensorData data) {
        var sensorMeasurementEvent = sensorMeasurementMapper.mapToSensorMeasurementEvent(topic, data);
        rabbitTemplate.convertAndSend(
                "sensor.exchange",
                "sensor.tracking",
                sensorMeasurementEvent);
    }
}
