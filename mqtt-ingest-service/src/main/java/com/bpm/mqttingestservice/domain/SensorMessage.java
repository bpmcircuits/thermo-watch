package com.bpm.mqttingestservice.domain;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorMessage {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("Time")
    private LocalDateTime time;

    @JsonProperty("TempUnit")
    private String temperatureUnit;

    private String sensorTopic;

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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getSensorTopic() {
        return sensorTopic;
    }

    public void setSensorTopic(String sensorTopic) {
        this.sensorTopic = sensorTopic;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "SensorMessage{" +
                "time=" + time +
                ", temperatureUnit='" + temperatureUnit + '\'' +
                ", sensorData=" + sensorData +
                '}';
    }
}
