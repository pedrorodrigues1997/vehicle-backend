package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.RestRequests.MissionReportDTO;
import com.example.vehicle_backend.dto.RestRequests.VehicleMissionDataDTO;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.entities.Vehicle;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MissionReportService {

    private final MissionRepository missionRepository;
    private final VehicleRepository vehicleRepository;

    public MissionReportService(MissionRepository missionRepository, VehicleRepository vehicleRepository) {
        this.missionRepository = missionRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<MissionReportDTO> getMissionReport() {
        List<Mission> missions = missionRepository.findAll();
        List<MissionReportDTO> report = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        for (Mission mission : missions) {
            List<VehicleMissionDataDTO> vehicleDataDTOs = new ArrayList<>();

            for (VehicleMissionData vmd : mission.getVehicleMissionDataList()) {
                Vehicle vehicle = vehicleRepository.findByVin(vmd.getVehicleId())
                        .orElse(null);

                VehicleMissionDataDTO vmdDTO = new VehicleMissionDataDTO(vmd, vehicle);
                vehicleDataDTOs.add(vmdDTO);
            }

            MissionReportDTO missionDTO = new MissionReportDTO(mission, vehicleDataDTOs);
            try {
                CommonDataValidator.validate(missionDTO);
                report.add(missionDTO);
            } catch (IllegalArgumentException e) {
                validationErrors.add("Mission ID " + mission.getMissionId() + " validation failed: " + e.getMessage());
            }
        }

        if (!validationErrors.isEmpty()) {
            System.err.println("Validation errors found: " + validationErrors);
        }

        return report;
    }
}
