package com.bpm.measurementqueryservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NamedQueries({
        @NamedQuery(
                name = "Measurement.findMeasurementsBySensorFkForPeriodOfTime",
                query = "SELECT m FROM Measurement m WHERE m.sensorId = :ID AND m.timestamp >= :AFTER ORDER BY m.timestamp ASC"
        )
})

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "measurement")
public class Measurement {

    @Id
    private Long id;

    @Column(name = "temperature", precision = 5, scale = 2)
    private BigDecimal temperature;
    @Column(name = "humidity", precision = 5, scale = 2)
    private BigDecimal humidity;
    @Column(name = "dew_point", precision = 5, scale = 2)
    private BigDecimal dewPoint;
    @Column(name = "pressure", precision = 7, scale = 2)
    private BigDecimal pressure;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name = "sensor_fk")
    private Long sensorId;
}
