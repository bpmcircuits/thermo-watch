package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.Sensor;
import com.bpm.measurementqueryservice.dto.SensorDTO;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SensorMapper {

    public SensorDTO mapToSensorDTO(Sensor sensor) {
        return SensorDTO.builder()
                .id(sensor.getId())
                .sensorId(sensor.getSensorId())
                .location(sensor.getLocation())
                .sensorType(sensor.getSensorType())
                .lastSeen(getLocalDateTimeFormatted(sensor))
                .isOnline(sensor.getIsOnline())
                .build();
    }

    public List<SensorDTO> mapToSensorDTOList(List<Sensor> sensors) {
        return sensors.stream()
                .map(this::mapToSensorDTO)
                .toList();
    }

    private String getLocalDateTimeFormatted(Sensor sensor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return sensor.getLastSeen().format(formatter);
    }
}
