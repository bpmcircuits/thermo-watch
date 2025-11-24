package com.bpm.mqttingestservice.mqtt;

import com.bpm.mqttingestservice.service.MqttIngestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MqttMessageHandlerTest {

    @Mock
    private MqttIngestService ingestService;

    @InjectMocks
    private MqttMessageHandler messageHandler;

    private String testPayload;
    private String testTopic;

    @BeforeEach
    void setUp() {
        testPayload = """
                {
                    "Time": "2024-01-15T10:30:45",
                    "TempUnit": "C",
                    "DHT11": {
                        "Temperature": 23.5,
                        "Humidity": 60.0
                    }
                }
                """;
        testTopic = "temp_bathroom";
    }

    @Test
    void shouldHandleValidMessage() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", testTopic);
        Message<String> message = new GenericMessage<>(testPayload, headers);

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(ingestService, times(1)).ingest(testTopic, testPayload);
    }

    @Test
    void shouldExtractTopicFromHeaders() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", "temp_livingroom");
        Message<String> message = new GenericMessage<>(testPayload, headers);

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(ingestService).ingest("temp_livingroom", testPayload);
    }

    @Test
    void shouldConvertPayloadToString() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", testTopic);
        Message<Object> message = new GenericMessage<>(testPayload, headers);

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(ingestService).ingest(eq(testTopic), eq(testPayload));
    }

    @Test
    void shouldHandleExceptionFromIngestService() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", testTopic);
        Message<String> message = new GenericMessage<>(testPayload, headers);
        doThrow(new RuntimeException("Processing error")).when(ingestService).ingest(anyString(), anyString());

        // When & Then
        assertDoesNotThrow(() -> messageHandler.handleMessage(message));
        verify(ingestService).ingest(testTopic, testPayload);
    }

    @Test
    void shouldHandleMissingTopicHeader() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        Message<String> message = new GenericMessage<>(testPayload, headers);

        // When & Then
        assertDoesNotThrow(() -> messageHandler.handleMessage(message));
        verify(ingestService, never()).ingest(anyString(), anyString());
    }

    @Test
    void shouldHandleNullTopicHeader() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", null);
        Message<String> message = new GenericMessage<>(testPayload, headers);

        // When & Then
        assertDoesNotThrow(() -> messageHandler.handleMessage(message));
        verify(ingestService, never()).ingest(anyString(), anyString());
    }

    @Test
    void shouldHandleEmptyPayload() {
        // Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", testTopic);
        Message<String> message = new GenericMessage<>("", headers);

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(ingestService).ingest(testTopic, "");
    }

    @Test
    void shouldHandleMultipleMessages() {
        // Given
        Map<String, Object> headers1 = new HashMap<>();
        headers1.put("mqtt_receivedTopic", "temp_bathroom");
        Message<String> message1 = new GenericMessage<>(testPayload, headers1);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put("mqtt_receivedTopic", "temp_livingroom");
        Message<String> message2 = new GenericMessage<>(testPayload, headers2);

        // When
        messageHandler.handleMessage(message1);
        messageHandler.handleMessage(message2);

        // Then
        verify(ingestService).ingest("temp_bathroom", testPayload);
        verify(ingestService).ingest("temp_livingroom", testPayload);
        verify(ingestService, times(2)).ingest(anyString(), anyString());
    }
}
