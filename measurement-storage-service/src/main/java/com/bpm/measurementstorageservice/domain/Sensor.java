package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sensor")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id",nullable = false, unique = true)
    private String sensorId;
    @Column(name = "sensor_type")
    private String sensorType;
    @Column(name = "location", nullable = false)
    private String location;
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    @Column(name = "is_online")
    private Boolean isOnline;

    @OneToMany(mappedBy = "sensor")
    private List<Measurement> measurements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_data_fk")
    private RoomData roomData;

    public void setRoomData(RoomData roomData) {
        this.roomData = roomData;
        if (roomData != null && roomData.getSensors() != null && !roomData.getSensors().contains(this)) {
            roomData.getSensors().add(this);
        }
    }
}
