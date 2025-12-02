package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.Measurement;
import com.bpm.measurementqueryservice.dto.MeasurementDTO;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MeasurementMapper {

    public MeasurementDTO mapToMeasurementDTO(Measurement measurement) {

        return MeasurementDTO.builder()
                .id(measurement.getId().toString())
                .sensorId(measurement.getSensorId().toString())
                .temperature(measurement.getTemperature() != null ? measurement.getTemperature().doubleValue() : null)
                .humidity(measurement.getHumidity() != null ? measurement.getHumidity().doubleValue() : null)
                .dewPoint(measurement.getDewPoint() != null ? measurement.getDewPoint().doubleValue() : null)
                .pressure(measurement.getPressure() != null ? measurement.getPressure().doubleValue() : null)
                .timestamp(getLocalDateTimeFormatted(measurement))
                .build();
    }

    private String getLocalDateTimeFormatted(Measurement measurement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return measurement.getTimestamp().format(formatter);
    }

    public List<MeasurementDTO> mapToMeasurementDTOList(List<Measurement> measurements) {
        return measurements.stream().map(this::mapToMeasurementDTO).toList();
    }
}
