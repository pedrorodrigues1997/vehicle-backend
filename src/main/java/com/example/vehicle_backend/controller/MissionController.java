package com.example.vehicle_backend.controller;

import com.example.vehicle_backend.dto.RestRequests.RESTMissionRequest;
import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.services.MissionManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/missions")
public class MissionController {


    private final VehicleRepository vehicleRepository;
    private final MissionRepository missionRepository;
    private final MissionManager missionManager;

    @Autowired
    public MissionController(VehicleRepository vehicleRepository, MissionRepository missionRepository, MissionManager missionManager) {
        this.vehicleRepository = vehicleRepository;
        this.missionRepository = missionRepository;
        this.missionManager = missionManager;
    }

    @PostMapping("/start")
    public ResponseEntity<?> createMission(@Valid @RequestBody RESTMissionRequest request, BindingResult result) {

        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder("Validation errors: ");
            result.getFieldErrors().forEach(error -> {
                sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
            });
            return ResponseEntity.badRequest().body(sb.toString());
        }

        Set<String> uniqueVins = new HashSet<>(request.getAssignedVehicles());
        if (uniqueVins.size() != request.getAssignedVehicles().size()) {
            return ResponseEntity.badRequest().body("Duplicate VINs are not allowed.");
        }

        if (missionRepository.existsByMissionName(request.getMissionName())) {
            return ResponseEntity.badRequest().body("A mission with this name already exists.");
        }

        if (!request.getAssignedVehicles().stream().allMatch(vehicleRepository::existsByVin)) {
            return ResponseEntity.badRequest().body("Some of the provided VINs do not exist.");
        }

        List<String> busyCars = missionRepository.findVehiclesInOngoingMissions(request.getAssignedVehicles());
        if (!busyCars.isEmpty()) {
            return ResponseEntity.badRequest().body("Vehicle(s) already in a mission: " + busyCars);
        }


        System.out.printf("Received Mission Request: " + request);


        Mission mission = toMission(request);
        missionRepository.save(mission); //To Generate the missionId
        missionManager.registerMissionHandler(mission.getMissionId(), mission);
        missionManager.startMission(mission.getMissionId()); //Optionally we could leave this for another end point. We could have one to schedule, and one to start
        return ResponseEntity.ok(mission);
    }

    private Mission toMission(RESTMissionRequest request) {
        Mission mission = new Mission();
        mission.setMissionName(request.getMissionName());
        mission.setMissionDescription(request.getMissionDescription());
        mission.setGoal(request.getGoal());
        mission.setWaypoints(request.getWaypoints());
        mission.setStatus(MissionStatus.PENDING);
        //Creating an individual vehicle mission data for each VIN
        mission.addVehicleMissionData(request.getAssignedVehicles());

        return mission;
    }
}
