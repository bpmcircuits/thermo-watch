package com.bpm.measurementstorageservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "temperature", precision = 5, scale = 2)
    private BigDecimal temperature;
    @Column(name = "humidity", precision = 5, scale = 2)
    private BigDecimal humidity;
    @Column(name = "dew_point", precision = 5, scale = 2)
    private BigDecimal dewPoint;
    @Column(name = "pressure", precision = 5, scale = 2)
    private BigDecimal pressure;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_fk", nullable = false)
    private Sensor sensor;

    public void attachTo(Sensor sensor) {
        this.sensor = sensor;
        if (sensor.getMeasurements() != null && !sensor.getMeasurements().contains(this)) {
            sensor.getMeasurements().add(this);
        }
    }
}
