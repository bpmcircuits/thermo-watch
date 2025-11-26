package com.bpm.mqttingestservice.rabbit.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.mapper.SensorMeasurementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    /**
     * Wysyła zdarzenie pomiaru na podstawie całej wiadomości MQTT oraz danych sensora.
     */
    public void sendMeasurement(SensorMessage message, SensorData sensorData) {
        SensorMeasurementEvent sensorMeasurementEvent =
                sensorMeasurementMapper.mapToSensorMeasurementEvent(message, sensorData);

        rabbitTemplate.convertAndSend(
                exchangeName,
                measurementRoutingKey,
                sensorMeasurementEvent
        );
    }

    public void sendAvailability(String sensorId, String status) {
        SensorAvailabilityEvent event = SensorAvailabilityEvent.builder()
                .sensorId(sensorId)
                .status(status)
                .source("MQTT_LWT")
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(
                exchangeName,
                availabilityRoutingKey,
                event
        );
    }
}
