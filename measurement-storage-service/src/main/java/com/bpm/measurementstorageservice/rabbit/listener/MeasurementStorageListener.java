package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.measurementstorageservice.rabbit.dto.SensorAvailabilityEvent;
import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.service.MeasurementStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = "${rabbitmq.queue.name}")
public class MeasurementStorageListener {

    private final MeasurementStorageService measurementStorageService;
    private static final Logger logger = LoggerFactory.getLogger(MeasurementStorageListener.class);

    @RabbitHandler
    public void handleMeasurement(SensorMeasurementEvent event) {
        measurementStorageService.storeMeasurement(event);
    }

    @RabbitHandler
    public void handleAvailability(SensorAvailabilityEvent event) {
        measurementStorageService.storeAvailability(event);
    }
}
