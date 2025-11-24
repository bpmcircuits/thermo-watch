package com.bpm.mqttingestservice.domain;

import com.bpm.events.dto.SensorMeasurementEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DHT11DataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        // Given
        String json = """
                {
                    "Temperature": 23.5,
                    "Humidity": 60.0,
                    "DewPoint": 15.2
                }
                """;

        // When
        DHT11Data data = objectMapper.readValue(json, DHT11Data.class);

        // Then
        assertEquals(23.5, data.getTemperature());
        assertEquals(60.0, data.getHumidity());
        assertEquals(15.2, data.getDewPoint());
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        // Given
        String json = """
                {
                    "Temperature": 23.5,
                    "Humidity": 60.0,
                    "DewPoint": 15.2,
                    "UnknownField": "value"
                }
                """;

        // When & Then
        assertDoesNotThrow(() -> objectMapper.readValue(json, DHT11Data.class));
    }

    @Test
    void shouldConvertToMeasurementEvent() throws Exception {
        // Given
        DHT11Data data = createDHT11Data(25.0, 55.0, 14.5);
        String topic = "temp_bathroom";

        // When
        SensorMeasurementEvent event = data.toMeasurementEvent(topic);

        // Then
        assertEquals("DHT11", event.sensorType());
        assertEquals(topic, event.location());
        assertEquals(new BigDecimal("25"), event.temperature());
        assertEquals(new BigDecimal("55"), event.humidity());
        assertEquals(new BigDecimal("14.5"), event.dewPoint());
        assertNotNull(event.sensorId());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldGenerateConsistentSensorIdForSameTopic() throws Exception {
        // Given
        DHT11Data data1 = createDHT11Data(25.0, 55.0, 14.5);
        DHT11Data data2 = createDHT11Data(26.0, 56.0, 15.0);
        String topic = "temp_bathroom";

        // When
        String sensorId1 = data1.toMeasurementEvent(topic).sensorId();
        String sensorId2 = data2.toMeasurementEvent(topic).sensorId();

        // Then
        assertEquals(sensorId1, sensorId2);
    }

    @Test
    void shouldGenerateDifferentSensorIdForDifferentTopics() throws Exception {
        // Given
        DHT11Data data = createDHT11Data(25.0, 55.0, 14.5);
        String topic1 = "temp_bathroom";
        String topic2 = "temp_livingroom";

        // When
        String sensorId1 = data.toMeasurementEvent(topic1).sensorId();
        String sensorId2 = data.toMeasurementEvent(topic2).sensorId();

        // Then
        assertNotEquals(sensorId1, sensorId2);
    }

    private DHT11Data createDHT11Data(double temperature, double humidity, double dewPoint) throws Exception {
        String json = String.format("""
                {
                    "Temperature": %f,
                    "Humidity": %f,
                    "DewPoint": %f
                }
                """, temperature, humidity, dewPoint);

        return objectMapper.readValue(json, DHT11Data.class);
    }
}
