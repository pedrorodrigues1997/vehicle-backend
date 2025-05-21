package com.example.vehicle_backend.controller;

import com.example.vehicle_backend.dto.RestRequests.MissionReportDTO;
import com.example.vehicle_backend.services.MissionReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class MissionReportController {

    private final MissionReportService missionReportService;

    public MissionReportController(MissionReportService missionReportService) {
        this.missionReportService = missionReportService;
    }

    @GetMapping("/missions")
    public ResponseEntity<List<MissionReportDTO>> getMissionsReport() {
        List<MissionReportDTO> report = missionReportService.getMissionReport();
        return ResponseEntity.ok(report);
    }
}
