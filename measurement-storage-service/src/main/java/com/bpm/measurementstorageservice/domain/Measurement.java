package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "sensor_id")
    private String sensorId;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "humidity")
    private Double humidity;
    @Column(name = "dew_point")
    private Double dewPoint;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sensor_measurement_id")
    private Sensor sensor;
}
