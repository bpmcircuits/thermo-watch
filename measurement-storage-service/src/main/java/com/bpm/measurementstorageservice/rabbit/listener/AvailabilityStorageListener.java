package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.measurementstorageservice.service.AvailabilityStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = "${rabbitmq.queue.name}")
public class AvailabilityStorageListener {
    private final AvailabilityStorageService availabilityStorageService;

    @RabbitHandler
    public void handleAvailability(SensorAvailabilityEvent event) {
        availabilityStorageService.storeAvailability(event);
    }
}
