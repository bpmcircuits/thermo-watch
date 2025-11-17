package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.service.MeasurementStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasurementStorageListener {

    private final MeasurementStorageService measurementStorageService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(SensorMeasurementEvent event) {
        measurementStorageService.store(event);
    }
}
