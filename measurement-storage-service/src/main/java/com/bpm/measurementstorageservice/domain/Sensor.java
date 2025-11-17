package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sensor")
public class Sensor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "sensor_id")
    private String sensorId;
    @Column(name = "sensor_type")
    private String sensorType;
    @Column(name = "location")
    private String location;
    @Column(name = "timestamp")
    private String timestamp;
    @Column(name = "last_seen")
    private String lastSeen;
    @Column(name = "is_online")
    private Boolean isOnline;
}
