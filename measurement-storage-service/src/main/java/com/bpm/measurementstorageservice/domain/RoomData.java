package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "room_data")
public class RoomData {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "location")
    private String location;
    @Column(name = "current_temperature")
    private Double currentTemperature;
    @Column(name = "current_humidity")
    private Double currentHumidity;
    @Column(name = "sensor_count")
    private Integer sensorCount;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_data_id")
    private List<Sensor> sensors;
}
