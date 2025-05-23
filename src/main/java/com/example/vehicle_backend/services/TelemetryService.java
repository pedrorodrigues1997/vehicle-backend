package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.MqttResponses.MQTTStatusData;
import com.example.vehicle_backend.dto.MqttResponses.MQTTTelemetryData;
import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.TelemetryData;
import com.example.vehicle_backend.repositories.TelemetryDataRepository;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class TelemetryService {

    private final TelemetryDataRepository telemetryDataRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public TelemetryService(TelemetryDataRepository telemetryDataRepository, VehicleRepository vehicleRepository) {
        this.telemetryDataRepository = telemetryDataRepository;
        this.vehicleRepository = vehicleRepository;
    }


    public void processTelemetry(MQTTTelemetryData dto) {

        if (!vehicleRepository.existsByVin(dto.getVin())) {
            throw new IllegalArgumentException("Received telemetry from unregistered vehicle: " + dto.getVin());
        }

        CommonDataValidator.validate(dto);
        CommonDataValidator.validateTimestamp(dto.getTimestamp());


        TelemetryData entity = toEntity(dto);
        telemetryDataRepository.save(entity);
    }


    private TelemetryData toEntity(MQTTTelemetryData dto) {

        Location location = new Location();
        location.setLng(dto.getLng());
        location.setLat(dto.getLat());


        TelemetryData entity = new TelemetryData();
        entity.setVin(dto.getVin());
        entity.setTimestamp(Instant.ofEpochMilli(dto.getTimestamp()));
        entity.setLocation(location);
        entity.setSpeed(dto.getSpeed());
        return entity;
    }
}