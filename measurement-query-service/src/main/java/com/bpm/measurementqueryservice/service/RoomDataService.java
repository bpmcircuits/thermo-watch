package com.bpm.measurementqueryservice.service;

import com.bpm.measurementqueryservice.domain.RoomData;
import com.bpm.measurementqueryservice.repository.RoomDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomDataService {

    private final RoomDataRepository roomDataRepo;

    public List<RoomData> getRoomsDataInformation() {
        return roomDataRepo.findAll();
    }
}
