package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DHT11MeasurementMappingStrategy implements SensorMeasurementMappingStrategy<DHT11Data> {

    private static final String SENSOR_TYPE = "DHT11";

    @Override
    public Class<DHT11Data> getSupportedType() {
        return DHT11Data.class;
    }

    @Override
    public SensorMeasurementEvent toMeasurementEvent(SensorMessage message, DHT11Data data) {
        return SensorMeasurementEvent.builder()
                .sensorType(SENSOR_TYPE)
                .sensorId(message.getSensorId())
                .location(message.getLocation())
                .temperature(toBigDecimal(data.getTemperature()))
                .humidity(toBigDecimal(data.getHumidity()))
                .dewPoint(toBigDecimal(data.getDewPoint()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private BigDecimal toBigDecimal(double value) {
        return new BigDecimal(Double.toString(value));
    }
}
