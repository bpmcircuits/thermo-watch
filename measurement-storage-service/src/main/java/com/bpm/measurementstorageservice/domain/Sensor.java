package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime lastSeen;
    @Column(name = "is_online")
    private Boolean isOnline;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_measurement_id")
    private List<Measurement> measurements;

    @ManyToOne
    @JoinColumn(name = "room_data_id")
    private RoomData roomData;
}
