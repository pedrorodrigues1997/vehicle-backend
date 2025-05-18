package com.example.vehicle_backend.repositories;

import com.example.vehicle_backend.model.Mission;
import com.example.vehicle_backend.model.TelemetryData;
import com.example.vehicle_backend.model.TelemetryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
