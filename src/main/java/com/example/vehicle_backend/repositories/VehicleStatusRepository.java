package com.example.vehicle_backend.repositories;

import com.example.vehicle_backend.entities.TelemetryData;
import com.example.vehicle_backend.entities.TelemetryId;
import com.example.vehicle_backend.entities.vehicleStatus.VehicleStatusData;
import com.example.vehicle_backend.entities.vehicleStatus.VehicleStatusDataId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleStatusRepository extends JpaRepository<VehicleStatusData, VehicleStatusDataId> {
}
