package com.bpm.measurementqueryservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sensor")
public class Sensor {

    @Id
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
    private LocalDateTime lastSeen;
    @Column(name = "is_online")
    private Boolean isOnline;
    @Column(name = "room_data_fk")
    private Long roomDataId;
}
