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
        sensorMessage.setSensorId("sensor-1");
        sensorMessage.setLocation("HOME_BATHROOM");

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
        verify(sensorMeasurementService).sendMeasurement(sensorMessage, dht11Data);
    }

    @Test
    void shouldUseMetadataFromMessage() {
        // Given
        sensorMessage.setSensorId("sensor-2");
        sensorMessage.setLocation("HOME_KITCHEN");

        // When
        strategy.processSensorData(dht11Data, sensorMessage);

        // Then
        verify(sensorMeasurementService).sendMeasurement(sensorMessage, dht11Data);
    }

    @Test
    void shouldHandleDifferentSensors() {
        // Given
        SensorMessage message1 = new SensorMessage();
        message1.setSensorId("sensor-1");
        message1.setLocation("HOME_BATHROOM");
        DHT11Data data1 = mock(DHT11Data.class);

        SensorMessage message2 = new SensorMessage();
        message2.setSensorId("sensor-2");
        message2.setLocation("HOME_KITCHEN");
        DHT11Data data2 = mock(DHT11Data.class);

        // When
        strategy.processSensorData(data1, message1);
        strategy.processSensorData(data2, message2);

        // Then
        verify(sensorMeasurementService).sendMeasurement(message1, data1);
        verify(sensorMeasurementService).sendMeasurement(message2, data2);
    }
}