package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DS18B20ProcessingStrategyTest {

    @Mock
    private SensorMeasurementService sensorMeasurementService;

    @InjectMocks
    private DS18B20ProcessingStrategy strategy;

    private SensorMessage sensorMessage;
    private DS18B20Data ds18B20Data;

    @BeforeEach
    void setUp() {
        sensorMessage = new SensorMessage();
        sensorMessage.setSensorId("sensor-1");
        sensorMessage.setLocation("HOME_BATHROOM");

        ds18B20Data = mock(DS18B20Data.class);
    }

    @Test
    void shouldReturnDS18B20SensorType() {
        // When
        String result = strategy.getSensorType();

        // Then
        assertEquals("DS18B20", result);
    }

    @Test
    void shouldReturnDS18B20DataClass() {
        // When
        Class<DS18B20Data> result = strategy.getDataClass();

        // Then
        assertEquals(DS18B20Data.class, result);
    }

    @Test
    void shouldProcessDS18B20Data() {
        // When
        strategy.processSensorData(ds18B20Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendMeasurement(sensorMessage, ds18B20Data);
    }

    @Test
    void shouldUseMetadataFromMessage() {
        // Given
        sensorMessage.setSensorId("sensor-2");
        sensorMessage.setLocation("HOME_KITCHEN");

        // When
        strategy.processSensorData(ds18B20Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendMeasurement(sensorMessage, ds18B20Data);
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorId("sensor-1");
        message1.setLocation("HOME_BATHROOM");
        DS18B20Data data1 = mock(DS18B20Data.class);

        SensorMessage message2 = new SensorMessage();
        message2.setSensorId("sensor-2");
        message2.setLocation("HOME_KITCHEN");
        DS18B20Data data2 = mock(DS18B20Data.class);

        // When
        strategy.processSensorData(data1, message1);
        strategy.processSensorData(data2, message2);

        // Then
        verify(sensorMeasurementService).sendMeasurement(message1, data1);
        verify(sensorMeasurementService).sendMeasurement(message2, data2);
    }
}