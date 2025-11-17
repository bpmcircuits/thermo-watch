package com.bpm.measurementqueryservice.domain;

import jakarta.persistence.*;
import lombok.Getter;

@NamedQuery(
        name = "Sensor.findSensorBySensorIdForPeriodOfTime",
        query = "FROM sensor WHERE sensor_id = :ID AND timestamp <= :TIME"
)

@Entity
@Getter
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
    private String lastSeen;
    @Column(name = "is_online")
    private Boolean isOnline;
}
