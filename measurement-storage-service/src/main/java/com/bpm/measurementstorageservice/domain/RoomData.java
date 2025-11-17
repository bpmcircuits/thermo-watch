package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "room_data")
public class RoomData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location", nullable = false, unique = true)
    private String location;
    @Column(name = "current_temperature", precision = 5, scale = 2)
    private BigDecimal currentTemperature;
    @Column(name = "current_humidity", precision = 5, scale = 2)
    private BigDecimal currentHumidity;
    @Column(name = "sensor_count")
    private Integer sensorCount;

    @OneToMany(mappedBy = "roomData")
    private List<Sensor> sensors;
}
