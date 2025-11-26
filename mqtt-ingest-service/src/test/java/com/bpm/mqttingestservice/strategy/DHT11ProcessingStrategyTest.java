package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DHT11Data;
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
class DHT11ProcessingStrategyTest {

    @Mock
    private SensorMeasurementService sensorMeasurementService;

    @InjectMocks
    private DHT11ProcessingStrategy strategy;

    private SensorMessage sensorMessage;
    private DHT11Data dht11Data;

    @BeforeEach
    void setUp() {
        sensorMessage = new SensorMessage();
        sensorMessage.setSensorTopic("temp_bathroom");

        dht11Data = mock(DHT11Data.class);
    }

    @Test
    void shouldReturnDHT11SensorType() {
        // When
        String result = strategy.getSensorType();

        // Then
        assertEquals("DHT11", result);
    }

    @Test
    void shouldReturnDHT11DataClass() {
        // When
        Class<DHT11Data> result = strategy.getDataClass();

        // Then
        assertEquals(DHT11Data.class, result);
    }

    @Test
    void shouldProcessDHT11Data() {
        // When
        strategy.processSensorData(dht11Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendMeasurement("temp_bathroom", dht11Data);
    }

    @Test
    void shouldUseTopicFromMessage() {
        // Given
        sensorMessage.setSensorTopic("temp_kitchen");

        // When
        strategy.processSensorData(dht11Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendMeasurement("temp_kitchen", dht11Data);
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorTopic("temp_bathroom");
        DHT11Data data1 = mock(DHT11Data.class);

        SensorMessage message2 = new SensorMessage();
        message2.setSensorTopic("temp_kitchen");
        DHT11Data data2 = mock(DHT11Data.class);

        // When
        strategy.processSensorData(data1, message1);
        strategy.processSensorData(data2, message2);

        // Then
        verify(sensorMeasurementService).sendMeasurement("temp_bathroom", data1);
        verify(sensorMeasurementService).sendMeasurement("temp_kitchen", data2);
    }
}