package com.bpm.mqttingestservice.rabbit.mapper;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.domain.SensorMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorMeasurementMapper {

    private final Map<Class<? extends SensorData>, SensorMeasurementMappingStrategy<?>> strategies = new HashMap<>();

    public SensorMeasurementMapper(List<SensorMeasurementMappingStrategy<?>> strategyList) {
        for (SensorMeasurementMappingStrategy<?> strategy : strategyList) {
            strategies.put(strategy.getSupportedType(), strategy);
        }
    }

    public SensorMeasurementEvent mapToSensorMeasurementEvent(SensorMessage message, SensorData sensorData) {
        if (sensorData == null) {
            throw new IllegalArgumentException("sensorData cannot be null");
        }

        SensorMeasurementMappingStrategy<?> strategy = strategies.get(sensorData.getClass());
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No SensorMeasurementMappingStrategy found for type: " + sensorData.getClass().getName()
            );
        }

        return invokeStrategy(strategy, message, sensorData);
    }

    @SuppressWarnings("unchecked")
    private <T extends SensorData> SensorMeasurementEvent invokeStrategy(
            SensorMeasurementMappingStrategy<?> rawStrategy,
            SensorMessage message,
            SensorData sensorData
    ) {
        SensorMeasurementMappingStrategy<T> typedStrategy =
                (SensorMeasurementMappingStrategy<T>) rawStrategy;
        T typedData = (T) sensorData;
        return typedStrategy.toMeasurementEvent(message, typedData);
    }
}
