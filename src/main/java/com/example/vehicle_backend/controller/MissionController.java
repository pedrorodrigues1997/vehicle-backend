package com.example.vehicle_backend.controller;

import com.example.vehicle_backend.dto.MissionRequest;
import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.model.Mission;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.example.vehicle_backend.services.MissionManager;
import com.example.vehicle_backend.topicHandlers.MissionHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private final MissionManager missionManager;

    @Autowired
    public MissionController(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @PostMapping("/start")
    public ResponseEntity<?> createMission(@Valid @RequestBody MissionRequest request, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Mission mission = toMission(request);
        Mission savedMission = missionRepository.save(mission);


        missionManager.registerMissionHandler(savedMission.getMissionId(), savedMission);

        missionManager.startMission(savedMission.getMissionId());
        return ResponseEntity.ok(savedMission);
    }

    private Mission toMission(MissionRequest request) {
        Mission mission = new Mission();
        mission.setMissionName(request.getMissionName());
        mission.setMissionDescription(request.getMissionDescription());
        mission.setAssignedVehicles(request.getAssignedVehicles());
        mission.setGoal(request.getGoal());
        mission.setWaypoints(request.getWaypoints());
        mission.setStatus(MissionStatus.PENDING);
        return mission;
    }
}
