package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.MqttResponses.MQTTStatusData;
import com.example.vehicle_backend.entities.vehicleStatus.VehicleStatusData;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.repositories.VehicleStatusRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
public class VehicleStatusService {

    private final VehicleStatusRepository vehicleStatusRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleStatusService(VehicleStatusRepository vehicleStatusRepository, VehicleRepository vehicleRepository) {
        this.vehicleStatusRepository = vehicleStatusRepository;
        this.vehicleRepository = vehicleRepository;
    }


    public void processVehicleStatus(MQTTStatusData dto) {

        if (!vehicleRepository.existsByVin(dto.getVehicleId())) {
            throw new IllegalArgumentException("Received vehicle status from unregistered vehicle: " + dto.getVehicleId());
        }

        CommonDataValidator.validate(dto);
        CommonDataValidator.validateTimestamp(dto.getTimestamp());

        VehicleStatusData entity = toEntity(dto);
        vehicleStatusRepository.save(entity);
    }


    private VehicleStatusData toEntity(MQTTStatusData dto) {
        VehicleStatusData entity = new VehicleStatusData();
        entity.setVehicleId(dto.getVehicleId());
        entity.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.getTimestamp()), ZoneId.systemDefault()));
        entity.setEngineStatus(dto.getEngineStatus());
        entity.setEngineOilLevelPercent(dto.getEngineOilLevelPercent());
        entity.setEngineCheckEngineLight(dto.isEngineCheckEngineLight());
        entity.setBatteryStatus(dto.getBatteryStatus());
        entity.setBatteryVoltage(dto.getBatteryVoltage());
        entity.setTireFrontLeftPsi(dto.getTireFrontLeftPsi());
        entity.setTireFrontRightPsi(dto.getTireFrontRightPsi());
        entity.setTireRearLeftPsi(dto.getTireRearLeftPsi());
        entity.setTireRearRightPsi(dto.getTireRearRightPsi());
        entity.setBrakeStatus(dto.getBrakeStatus());
        return entity;
    }
}
