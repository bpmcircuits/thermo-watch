package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.service.MeasurementStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = "${rabbitmq.queue.measurement}")
public class MeasurementStorageListener {

    private final MeasurementStorageService measurementStorageService;

    @RabbitHandler
    public void handleMeasurement(SensorMeasurementEvent event) {
        measurementStorageService.storeMeasurement(event);
    }
}
