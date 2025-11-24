package com.bpm.mqttingestservice.strategy;

import com.bpm.mqttingestservice.domain.DHT11Data;
import com.bpm.mqttingestservice.domain.DS18B20Data;
import com.bpm.mqttingestservice.domain.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorProcessingContextTest {

    @Mock
    private SensorProcessingStrategy dht11Strategy;

    @Mock
    private SensorProcessingStrategy ds18b20Strategy;

    @Mock
    private SensorProcessingStrategy availabilityStrategy;

    @Mock
    private ObjectMapper objectMapper;

    private SensorProcessingContext context;

    @BeforeEach
    void setUp() {
        when(dht11Strategy.getSensorType()).thenReturn("DHT11");
        when(ds18b20Strategy.getSensorType()).thenReturn("DS18B20");
        when(availabilityStrategy.getSensorType()).thenReturn("AVAILABILITY");

        List<SensorProcessingStrategy> strategies = List.of(dht11Strategy, ds18b20Strategy, availabilityStrategy);
        context = new SensorProcessingContext(strategies, objectMapper);

        clearInvocations(dht11Strategy, ds18b20Strategy, availabilityStrategy);
    }

    @Test
    void shouldProcessAvailabilityMessage() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setAvailability("online");

        // When
        context.processSensorMessage(message);

        // Then
        verify(availabilityStrategy).processSensorData("online", message);
        verify(dht11Strategy, never()).processSensorData(any(), any());
        verify(ds18b20Strategy, never()).processSensorData(any(), any());
    }

    @Test
    void shouldProcessDHT11Data() {
        // Given
        SensorMessage message = new SensorMessage();
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("temperature", 25.5);
        rawData.put("humidity", 60.0);
        message.addSensorData("DHT11", rawData);

        DHT11Data typedData = new DHT11Data();
        when(dht11Strategy.getDataClass()).thenAnswer(invocation -> DHT11Data.class);
        when(objectMapper.convertValue(rawData, DHT11Data.class)).thenReturn(typedData);

        // When
        context.processSensorMessage(message);

        // Then
        ArgumentCaptor<DHT11Data> dataCaptor = ArgumentCaptor.forClass(DHT11Data.class);
        verify(dht11Strategy).processSensorData(dataCaptor.capture(), eq(message));
        assertEquals(typedData, dataCaptor.getValue());
    }

    @Test
    void shouldProcessDS18B20Data() {
        // Given
        SensorMessage message = new SensorMessage();
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("temperature", 22.3);
        message.addSensorData("DS18B20", rawData);

        DS18B20Data typedData = new DS18B20Data();
        when(ds18b20Strategy.getDataClass()).thenAnswer(invocation -> DS18B20Data.class);
        when(objectMapper.convertValue(rawData, DS18B20Data.class)).thenReturn(typedData);

        // When
        context.processSensorMessage(message);

        // Then
        verify(ds18b20Strategy).processSensorData(typedData, message);
    }

    @Test
    void shouldProcessMultipleSensorTypes() {
        // Given
        SensorMessage message = new SensorMessage();
        Map<String, Object> rawData1 = new HashMap<>();
        Map<String, Object> rawData2 = new HashMap<>();
        message.addSensorData("DHT11", rawData1);
        message.addSensorData("DS18B20", rawData2);

        DHT11Data typedData1 = new DHT11Data();
        DS18B20Data typedData2 = new DS18B20Data();
        when(dht11Strategy.getDataClass()).thenAnswer(invocation -> DHT11Data.class);
        when(ds18b20Strategy.getDataClass()).thenAnswer(invocation -> DS18B20Data.class);
        when(objectMapper.convertValue(rawData1, DHT11Data.class)).thenReturn(typedData1);
        when(objectMapper.convertValue(rawData2, DS18B20Data.class)).thenReturn(typedData2);

        // When
        context.processSensorMessage(message);

        // Then
        verify(dht11Strategy).processSensorData(typedData1, message);
        verify(ds18b20Strategy).processSensorData(typedData2, message);
    }

    @Test
    void shouldIgnoreUnknownSensorType() {
        // Given
        SensorMessage message = new SensorMessage();
        message.addSensorData("UNKNOWN", new HashMap<>());

        // When
        context.processSensorMessage(message);

        // Then
        verify(dht11Strategy, never()).processSensorData(any(), any());
        verify(ds18b20Strategy, never()).processSensorData(any(), any());
        verify(availabilityStrategy, never()).processSensorData(any(), any());
    }

    @Test
    void shouldNotProcessSensorDataWhenConversionFails() {
        // Given
        SensorMessage message = new SensorMessage();
        Map<String, Object> rawData = new HashMap<>();
        message.addSensorData("DHT11", rawData);

        when(dht11Strategy.getDataClass()).thenAnswer(invocation -> DHT11Data.class);
        when(objectMapper.convertValue(rawData, DHT11Data.class))
                .thenThrow(new IllegalArgumentException("Conversion error"));

        // When
        context.processSensorMessage(message);

        // Then
        verify(dht11Strategy, never()).processSensorData(any(), any());
    }

    @Test
    void shouldPrioritizeAvailabilityOverSensorData() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setAvailability("offline");
        message.addSensorData("DHT11", new HashMap<>());

        // When
        context.processSensorMessage(message);

        // Then
        verify(availabilityStrategy).processSensorData("offline", message);
        verify(dht11Strategy, never()).processSensorData(any(), any());
    }

    @Test
    void shouldHandleEmptySensorData() {
        // Given
        SensorMessage message = new SensorMessage();

        // When
        context.processSensorMessage(message);

        // Then
        verifyNoInteractions(dht11Strategy, ds18b20Strategy, availabilityStrategy);
    }
}
