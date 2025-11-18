package com.bpm.measurementqueryservice.repository;

import com.bpm.measurementqueryservice.domain.RoomData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDataRepository extends JpaRepository<RoomData, Long> {
}
