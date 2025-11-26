package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DS18B20MeasurementMappingStrategy implements SensorMeasurementMappingStrategy<DS18B20Data> {

    private static final String SENSOR_TYPE = "DS18B20";

    @Override
    public Class<DS18B20Data> getSupportedType() {
        return DS18B20Data.class;
    }

    @Override
    public SensorMeasurementEvent toMeasurementEvent(SensorMessage message, DS18B20Data data) {
        return SensorMeasurementEvent.builder()
                .sensorType(SENSOR_TYPE)
                .sensorId(message.getSensorId())
                .location(message.getLocation())
                .temperature(toBigDecimal(data.getTemperature()))
                .humidity(null)
                .dewPoint(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private BigDecimal toBigDecimal(double value) {
        return new BigDecimal(Double.toString(value));
    }
}