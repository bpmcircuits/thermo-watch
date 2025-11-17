package com.bpm.measurementstorageservice.repository;

import com.bpm.measurementstorageservice.domain.RoomData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDataStorageRepository extends JpaRepository<RoomData, Long> {
}
