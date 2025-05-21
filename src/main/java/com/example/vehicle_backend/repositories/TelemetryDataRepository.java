package com.example.vehicle_backend.repositories;

import com.example.vehicle_backend.entities.TelemetryData;
import com.example.vehicle_backend.entities.TelemetryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryDataRepository extends JpaRepository<TelemetryData, TelemetryId> {
}
