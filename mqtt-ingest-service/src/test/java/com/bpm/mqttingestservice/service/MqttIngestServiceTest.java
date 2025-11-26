package com.bpm.mqttingestservice.service;

import com.bpm.mqttingestservice.domain.SensorMessage;
import com.bpm.mqttingestservice.strategy.SensorProcessingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MqttIngestServiceTest {

    @Mock
    private SensorMessageParser parser;

    @Mock
    private SensorProcessingContext processingContext;

    @InjectMocks
    private MqttIngestService service;

    private String testTopic;
    private String testPayload;
    private SensorMessage testMessage;

    @BeforeEach
    void setUp() {
        testTopic = "temp_bathroom";
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
        testMessage = mock(SensorMessage.class);
    }

    @Test
    void shouldParseAndProcessMessage() {
        // Given
        when(parser.parse(testTopic, testPayload)).thenReturn(testMessage);

        // When
        service.ingest(testTopic, testPayload);

        // Then
        verify(parser).parse(testTopic, testPayload);
        verify(processingContext).processMessage(testMessage);
    }

    @Test
    void shouldPassTopicAndPayloadToParser() {
        // Given
        when(parser.parse(testTopic, testPayload)).thenReturn(testMessage);

        // When
        service.ingest(testTopic, testPayload);

        // Then
        verify(parser).parse(eq(testTopic), eq(testPayload));
    }

    @Test
    void shouldPassParsedMessageToProcessingContext() {
        // Given
        SensorMessage expectedMessage = mock(SensorMessage.class);
        when(parser.parse(anyString(), anyString())).thenReturn(expectedMessage);

        // When
        service.ingest(testTopic, testPayload);

        // Then
        verify(processingContext).processMessage(expectedMessage);
    }

    @Test
    void shouldHandleDifferentTopics() {
        // Given
        String topic1 = "temp_bathroom";
        String topic2 = "temp_kitchen";
        SensorMessage message1 = mock(SensorMessage.class);
        SensorMessage message2 = mock(SensorMessage.class);
        when(parser.parse(topic1, testPayload)).thenReturn(message1);
        when(parser.parse(topic2, testPayload)).thenReturn(message2);

        // When
        service.ingest(topic1, testPayload);
        service.ingest(topic2, testPayload);

        // Then
        verify(parser).parse(topic1, testPayload);
        verify(parser).parse(topic2, testPayload);
        verify(processingContext).processMessage(message1);
        verify(processingContext).processMessage(message2);
    }

    @Test
    void shouldPropagateParserException() {
        // Given
        when(parser.parse(testTopic, testPayload)).thenThrow(new RuntimeException("Parse error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> service.ingest(testTopic, testPayload));
        verify(parser).parse(testTopic, testPayload);
        verify(processingContext, never()).processMessage(any());
    }

    @Test
    void shouldPropagateProcessingContextException() {
        // Given
        when(parser.parse(testTopic, testPayload)).thenReturn(testMessage);
        doThrow(new RuntimeException("Processing error")).when(processingContext).processMessage(testMessage);

        // When & Then
        assertThrows(RuntimeException.class, () -> service.ingest(testTopic, testPayload));
        verify(parser).parse(testTopic, testPayload);
        verify(processingContext).processMessage(testMessage);
    }

    @Test
    void shouldHandleEmptyPayload() {
        // Given
        String emptyPayload = "";
        when(parser.parse(testTopic, emptyPayload)).thenReturn(testMessage);

        // When
        service.ingest(testTopic, emptyPayload);

        // Then
        verify(parser).parse(testTopic, emptyPayload);
        verify(processingContext).processMessage(testMessage);
    }
}
