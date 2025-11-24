package com.bpm.mqttingestservice.rabbit.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.rabbit.mapper.SensorMeasurementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorMeasurementServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SensorMeasurementMapper sensorMeasurementMapper;

    @InjectMocks
    private SensorMeasurementService service;

    @Captor
    private ArgumentCaptor<SensorAvailabilityEvent> availabilityEventCaptor;

    private String exchangeName;
    private String measurementRoutingKey;
    private String availabilityRoutingKey;

    @BeforeEach
    void setUp() {
        exchangeName = "sensor.exchange";
        measurementRoutingKey = "sensor.measurement";
        availabilityRoutingKey = "sensor.availability";

        ReflectionTestUtils.setField(service, "exchangeName", exchangeName);
        ReflectionTestUtils.setField(service, "measurementRoutingKey", measurementRoutingKey);
        ReflectionTestUtils.setField(service, "availabilityRoutingKey", availabilityRoutingKey);
    }

    @Test
    void shouldSendMeasurementEvent() {
        // Given
        String topic = "temp_bathroom";
        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = new SensorMeasurementEvent(
                "DHT11",
                "sensor-001",
                topic,
                new BigDecimal("23.5"),
                new BigDecimal("60.0"),
                new BigDecimal("15.2"),
                LocalDateTime.now()
        );
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(topic, sensorData)).thenReturn(event);

        // When
        service.send(topic, sensorData);

        // Then
        verify(sensorMeasurementMapper).mapToSensorMeasurementEvent(topic, sensorData);
        verify(rabbitTemplate).convertAndSend(exchangeName, measurementRoutingKey, event);
    }

    @Test
    void shouldUseCorrectExchangeAndRoutingKeyForMeasurement() {
        // Given
        String topic = "temp_livingroom";
        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(topic, sensorData)).thenReturn(event);

        // When
        service.send(topic, sensorData);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(measurementRoutingKey),
                eq(event)
        );
    }

    @Test
    void shouldSendAvailabilityEvent() {
        // Given
        String sensorLocation = "temp_bathroom";
        String status = "online";

        // When
        service.sendAvailability(sensorLocation, status);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                availabilityEventCaptor.capture()
        );

        SensorAvailabilityEvent capturedEvent = availabilityEventCaptor.getValue();
        assertEquals(sensorLocation, capturedEvent.sensorLocation());
        assertEquals(status, capturedEvent.status());
        assertEquals("MQTT_LWT", capturedEvent.source());
        assertNotNull(capturedEvent.timestamp());
    }

    @Test
    void shouldSendAvailabilityWithOfflineStatus() {
        // Given
        String sensorLocation = "temp_kitchen";
        String status = "offline";

        // When
        service.sendAvailability(sensorLocation, status);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                availabilityEventCaptor.capture()
        );

        SensorAvailabilityEvent capturedEvent = availabilityEventCaptor.getValue();
        assertEquals(sensorLocation, capturedEvent.sensorLocation());
        assertEquals("offline", capturedEvent.status());
    }

    @Test
    void shouldSetSourceAsMqttLwtInAvailabilityEvent() {
        // Given
        String sensorLocation = "temp_bathroom";
        String status = "online";

        // When
        service.sendAvailability(sensorLocation, status);

        // Then
        verify(rabbitTemplate).convertAndSend(
                anyString(),
                anyString(),
                availabilityEventCaptor.capture()
        );

        assertEquals("MQTT_LWT", availabilityEventCaptor.getValue().source());
    }

    @Test
    void shouldHandleMultipleMeasurementSends() {
        // Given
        String topic1 = "temp_bathroom";
        String topic2 = "temp_livingroom";
        SensorData sensorData1 = mock(SensorData.class);
        SensorData sensorData2 = mock(SensorData.class);
        SensorMeasurementEvent event1 = mock(SensorMeasurementEvent.class);
        SensorMeasurementEvent event2 = mock(SensorMeasurementEvent.class);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(topic1, sensorData1)).thenReturn(event1);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(topic2, sensorData2)).thenReturn(event2);

        // When
        service.send(topic1, sensorData1);
        service.send(topic2, sensorData2);

        // Then
        verify(rabbitTemplate).convertAndSend(exchangeName, measurementRoutingKey, event1);
        verify(rabbitTemplate).convertAndSend(exchangeName, measurementRoutingKey, event2);
        verify(rabbitTemplate, times(2)).convertAndSend(
                eq(exchangeName),
                eq(measurementRoutingKey),
                any(SensorMeasurementEvent.class)
        );
    }

    @Test
    void shouldHandleMultipleAvailabilitySends() {
        // Given
        String location1 = "temp_bathroom";
        String location2 = "temp_kitchen";

        // When
        service.sendAvailability(location1, "online");
        service.sendAvailability(location2, "offline");

        // Then
        verify(rabbitTemplate, times(2)).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                any(SensorAvailabilityEvent.class)
        );
    }
}
