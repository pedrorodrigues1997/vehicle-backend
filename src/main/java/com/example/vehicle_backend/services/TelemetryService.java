package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.TelemetryDataDTO;
import com.example.vehicle_backend.model.TelemetryData;
import com.example.vehicle_backend.repositories.TelemetryDataRepository;
import com.example.vehicle_backend.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TelemetryService {

    private final TelemetryDataRepository telemetryDataRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public TelemetryService(TelemetryDataRepository telemetryDataRepository, VehicleRepository vehicleRepository) {
        this.telemetryDataRepository = telemetryDataRepository;
        this.vehicleRepository = vehicleRepository;
    }


    public void processTelemetry(TelemetryDataDTO dto) {
        // Basic checks
        if (dto.getVin() == null || dto.getVin().isEmpty()) {
            throw new IllegalArgumentException("VIN cannot be null or empty");
        }

        if (!vehicleRepository.existsByVin(dto.getVin())) {
            throw new IllegalArgumentException("Received telemetry from unregistered vehicle: " + dto.getVin());
        }
        if (dto.getLat() < -90 || dto.getLat() > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }
        if (dto.getLng() < -180 || dto.getLng() > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        if (!isValidTimestamp(dto.getTimestamp())){
            throw new IllegalArgumentException("Invalid timestamp");
        }


        TelemetryData entity = toEntity(dto);
        telemetryDataRepository.save(entity);
    }

    private static boolean isValidTimestamp(long timestamp) {
        Instant now = Instant.now();
        Instant t = Instant.ofEpochMilli(timestamp);

        if (t.isAfter(now.plusSeconds(30)) || t.isBefore(now.minus(Duration.ofHours(1)))) {
            System.out.println("Timestamp too far in the future or past: " + timestamp);
            return false;
        }
        return true;
    }


    private TelemetryData toEntity(TelemetryDataDTO dto) {
        TelemetryData.Location location = new TelemetryData.Location();
        location.setLat(dto.getLat());
        location.setLng(dto.getLng());

        TelemetryData entity = new TelemetryData();
        entity.setVin(dto.getVin());
        entity.setTimestamp(Instant.ofEpochMilli(dto.getTimestamp()));
        entity.setLocation(location);
        entity.setSpeed(dto.getSpeed());
        entity.setEngineTemp(dto.getEngineTemp());
        entity.setFuelLevel(dto.getFuelLevel());
        return entity;
    }
}