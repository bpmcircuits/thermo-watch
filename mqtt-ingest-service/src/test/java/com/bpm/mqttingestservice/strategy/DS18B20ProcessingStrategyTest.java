package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.rabbit.service.SensorMeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DS18B20ProcessingStrategyTest {

    @Mock
    private SensorMeasurementService sensorMeasurementService;

    @InjectMocks
    private DS18B20ProcessingStrategy strategy;

    private SensorMessage sensorMessage;
    private DS18B20Data ds18B20Data;
    private DHT11Data dht11Data;

    @BeforeEach
    void setUp() {
        sensorMessage = new SensorMessage();
        sensorMessage.setSensorTopic("temp_bathroom");

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
        Class<?> result = strategy.getDataClass();

        // Then
        assertEquals(DS18B20Data.class, result);
    }

    @Test
    void shouldProcessDS18B20Data() {
        // When
        strategy.processSensorData(ds18B20Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).send("temp_bathroom", ds18B20Data);
    }

    @Test
    void shouldUseTopicFromMessage() {
        // Given
        sensorMessage.setSensorTopic("temp_kitchen");

        // When
        strategy.processSensorData(ds18B20Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).send("temp_kitchen", ds18B20Data);
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorTopic("temp_bathroom");
        DS18B20Data data1 = mock(DS18B20Data.class);

        SensorMessage message2 = new SensorMessage();
        message2.setSensorTopic("temp_kitchen");
        DS18B20Data data2 = mock(DS18B20Data.class);

        // When
        strategy.processSensorData(data1, message1);
        strategy.processSensorData(data2, message2);

        // Then
        verify(sensorMeasurementService).send("temp_bathroom", data1);
        verify(sensorMeasurementService).send("temp_kitchen", data2);
    }

    @Test
    void shouldCastDataToDS18B20Data() {
        // Given & When
        strategy.processSensorData(ds18B20Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).send("temp_bathroom", ds18B20Data);
    }
}
