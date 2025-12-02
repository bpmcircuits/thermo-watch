package com.bpm.mqttingestservice.rabbit.service;

import com.bpm.events.dto.SensorAvailabilityEvent;
import com.bpm.events.dto.SensorMeasurementEvent;
import com.bpm.mqttingestservice.domain.SensorData;
import com.bpm.mqttingestservice.domain.SensorMessage;
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
    void shouldSendMeasurementMeasurementEvent() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setSensorId("sensor-001");
        message.setLocation("HOME_BATHROOM");

        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = new SensorMeasurementEvent(
                "DHT11",
                "sensor-001",
                "HOME_BATHROOM",
                new BigDecimal("23.5"),
                new BigDecimal("60.0"),
                new BigDecimal("15.2"),
                new BigDecimal("1013.25"),
                LocalDateTime.now()
        );
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(message, sensorData)).thenReturn(event);

        // When
        service.sendMeasurement(message, sensorData);

        // Then
        verify(sensorMeasurementMapper).mapToSensorMeasurementEvent(message, sensorData);
        verify(rabbitTemplate).convertAndSend(exchangeName, measurementRoutingKey, event);
    }

    @Test
    void shouldUseCorrectExchangeAndRoutingKeyForMeasurement() {
        // Given
        SensorMessage message = new SensorMessage();
        message.setSensorId("sensor-002");
        message.setLocation("HOME_LIVINGROOM");

        SensorData sensorData = mock(SensorData.class);
        SensorMeasurementEvent event = mock(SensorMeasurementEvent.class);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(message, sensorData)).thenReturn(event);

        // When
        service.sendMeasurement(message, sensorData);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(measurementRoutingKey),
                eq(event)
        );
    }

    @Test
    void shouldSendMeasurementAvailabilityEvent() {
        // Given
        String sensorId = "sensor-001";
        String status = "online";

        // When
        service.sendAvailability(sensorId, status);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                availabilityEventCaptor.capture()
        );

        SensorAvailabilityEvent capturedEvent = availabilityEventCaptor.getValue();
        assertEquals(sensorId, capturedEvent.sensorId());
        assertEquals(status, capturedEvent.status());
        assertEquals("MQTT_LWT", capturedEvent.source());
        assertNotNull(capturedEvent.timestamp());
    }

    @Test
    void shouldSendMeasurementAvailabilityWithOfflineStatus() {
        // Given
        String sensorId = "sensor-002";
        String status = "offline";

        // When
        service.sendAvailability(sensorId, status);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                availabilityEventCaptor.capture()
        );

        SensorAvailabilityEvent capturedEvent = availabilityEventCaptor.getValue();
        assertEquals(sensorId, capturedEvent.sensorId());
        assertEquals("offline", capturedEvent.status());
    }

    @Test
    void shouldSetSourceAsMqttLwtInAvailabilityEvent() {
        // Given
        String sensorId = "sensor-003";
        String status = "online";

        // When
        service.sendAvailability(sensorId, status);

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
        SensorMessage message1 = new SensorMessage();
        message1.setSensorId("sensor-1");
        message1.setLocation("HOME_BATHROOM");

        SensorMessage message2 = new SensorMessage();
        message2.setSensorId("sensor-2");
        message2.setLocation("HOME_LIVINGROOM");

        SensorData sensorData1 = mock(SensorData.class);
        SensorData sensorData2 = mock(SensorData.class);
        SensorMeasurementEvent event1 = mock(SensorMeasurementEvent.class);
        SensorMeasurementEvent event2 = mock(SensorMeasurementEvent.class);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(message1, sensorData1)).thenReturn(event1);
        when(sensorMeasurementMapper.mapToSensorMeasurementEvent(message2, sensorData2)).thenReturn(event2);

        // When
        service.sendMeasurement(message1, sensorData1);
        service.sendMeasurement(message2, sensorData2);

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
        String sensorId1 = "sensor-1";
        String sensorId2 = "sensor-2";

        // When
        service.sendAvailability(sensorId1, "online");
        service.sendAvailability(sensorId2, "offline");

        // Then
        verify(rabbitTemplate, times(2)).convertAndSend(
                eq(exchangeName),
                eq(availabilityRoutingKey),
                any(SensorAvailabilityEvent.class)
        );
    }
}