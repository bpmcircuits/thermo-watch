package com.bpm.mqttingestservice.rabbit.service;

import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.rabbit.dto.SensorAvailabilityEvent;
import com.bpm.mqttingestservice.rabbit.mapper.SensorMeasurementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SensorMeasurementService {

    private final RabbitTemplate rabbitTemplate;
    private final SensorMeasurementMapper sensorMeasurementMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.measurement}")
    private String measurementRoutingKey;

    @Value("${rabbitmq.routing.availability}")
    private String availabilityRoutingKey;

    public void send(String topic, SensorData data) {
        var sensorMeasurementEvent = sensorMeasurementMapper.mapToSensorMeasurementEvent(topic, data);
        rabbitTemplate.convertAndSend(
                exchangeName,
                measurementRoutingKey,
                sensorMeasurementEvent);
    }

    public void sendAvailability(String sensorLocation, String status) {
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorLocation(sensorLocation)
                .status(status)
                .source("MQTT_LWT")
                .timestamp(Instant.now())
                .build();
        rabbitTemplate.convertAndSend(
                exchangeName,
                availabilityRoutingKey,
                event);
    }
}
