package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.BME280Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BME280MeasurementMappingStrategy implements SensorMeasurementMappingStrategy<BME280Data> {

    private static final String SENSOR_TYPE = "BME280";

    @Override
    public Class<BME280Data> getSupportedType() {
        return BME280Data.class;
    }

    @Override
    public SensorMeasurementEvent toMeasurementEvent(SensorMessage message, BME280Data data) {
        return SensorMeasurementEvent.builder()
                .sensorType(SENSOR_TYPE)
                .sensorId(message.getSensorId())
                .location(message.getLocation())
                .temperature(toBigDecimal(data.getTemperature()))
                .humidity(toBigDecimal(data.getHumidity()))
                .dewPoint(toBigDecimal(data.getDewPoint()))
                .pressure(toBigDecimal(data.getPressure()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private BigDecimal toBigDecimal(double value) {
        return new BigDecimal(Double.toString(value));
    }
}
