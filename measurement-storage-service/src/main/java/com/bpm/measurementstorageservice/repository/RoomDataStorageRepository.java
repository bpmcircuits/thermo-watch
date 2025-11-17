package com.bpm.measurementstorageservice.repository;

import com.bpm.measurementstorageservice.domain.RoomData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomDataStorageRepository extends JpaRepository<RoomData, Long> {
    Optional<RoomData> findByLocation(String location);
}
