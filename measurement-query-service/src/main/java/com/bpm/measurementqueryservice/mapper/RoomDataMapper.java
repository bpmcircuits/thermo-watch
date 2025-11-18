package com.bpm.measurementqueryservice.mapper;

import com.bpm.measurementqueryservice.domain.RoomData;
import com.bpm.measurementqueryservice.dto.RoomDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomDataMapper {

    public RoomDataDTO mapToRoomDataDTO(RoomData roomData) {
        return RoomDataDTO.builder()
                .location(roomData.getLocation())
                .currentTemperature(roomData.getCurrentTemperature() != null ? roomData.getCurrentTemperature().doubleValue() : null)
                .currentHumidity(roomData.getCurrentHumidity() != null ? roomData.getCurrentHumidity().doubleValue() : null)
                .sensorCount(roomData.getSensorCount())
                .build();
    }

    public List<RoomDataDTO> mapToRoomDataDTOList(List<RoomData> roomDataList) {
        return roomDataList.stream()
                .map(this::mapToRoomDataDTO)
                .toList();
    }
}
