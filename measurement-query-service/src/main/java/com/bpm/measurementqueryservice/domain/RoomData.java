package com.bpm.measurementqueryservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_data")
public class RoomData {

    @Id
    private Long id;
    @Column(name = "location")
    private String location;
    @Column(name = "current_temperature", precision = 5, scale = 2)
    private BigDecimal currentTemperature;
    @Column(name = "current_humidity", precision = 5, scale = 2)
    private BigDecimal currentHumidity;
    @Column(name = "current_pressure", precision = 7, scale = 2)
    private BigDecimal currentPressure;
    @Column(name = "sensor_count")
    private Integer sensorCount;

}
