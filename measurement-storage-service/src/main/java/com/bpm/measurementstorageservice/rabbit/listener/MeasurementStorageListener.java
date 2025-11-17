package com.bpm.measurementstorageservice.rabbit.listener;

import com.bpm.measurementstorageservice.mapper.MeasurementStorageMapper;
import com.bpm.measurementstorageservice.rabbit.dto.SensorMeasurementEvent;
import com.bpm.measurementstorageservice.service.MeasurementStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasurementStorageListener {

    private final MeasurementStorageService measurementStorageService;
    private final MeasurementStorageMapper measurementStorageMapper;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(SensorMeasurementEvent event) {
        measurementStorageService.storeSensorData(measurementStorageMapper.mapToSensor(event));
        measurementStorageService.storeMeasurementData(measurementStorageMapper.mapToMeasurement(event));
        measurementStorageService.storeRoomData(measurementStorageMapper.mapToRoomData(event));
    }
}
