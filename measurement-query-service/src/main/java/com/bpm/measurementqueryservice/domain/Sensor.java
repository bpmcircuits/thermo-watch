package com.bpm.measurementqueryservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "measurements")
public class Sensor {

    @Id
    private Long id;

    @Column(name = "sensor_type")
    private String sensorType;
    @Column(name = "sensor_id")
    private String sensorId;
    @Column(name = "location")
    private String location;
    @Column(name = "temperature")
    private String temperature;
    @Column(name = "humidity")
    private String humidity;
    @Column(name = "dew_point")
    private String dewPoint;
    @Column(name = "timestamp")
    private String timestamp;
}
