package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorMessage {
    @Setter
    @Getter
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("Time")
    private LocalDateTime time;

    @Setter
    @Getter
    @JsonProperty("TempUnit")
    private String temperatureUnit;

    @Setter
    @Getter
    private String sensorId;

    @Setter
    @Getter
    private String location;

    @Setter
    @Getter
    private String availability;

    private final Map<String, Object> sensorData = new HashMap<>();

    @JsonAnySetter
    public void addSensorData(String name, Object value) {
        sensorData.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getSensorData() {
        return sensorData;
    }

    @Override
    public String toString() {
        return "SensorMessage{" +
                "time=" + time +
                ", temperatureUnit='" + temperatureUnit + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", location='" + location + '\'' +
                ", availability='" + availability + '\'' +
                ", sensorData=" + sensorData +
                '}';
    }
}
