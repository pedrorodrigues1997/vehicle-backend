package com.example.vehicle_backend.dto;

import com.example.vehicle_backend.model.TelemetryData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.OffsetDateTime;
import java.util.List;

public class MissionRequest {
    @NotBlank
    private String missionName;
    @NotBlank
    private String missionDescription;
    @NotBlank
    private String goal;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Valid
    private List<TelemetryData.Location> waypoints;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Valid
    private List<String> assignedVehicles;


    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getMissionDescription() {
        return missionDescription;
    }

    public void setMissionDescription(String missionDescription) {
        this.missionDescription = missionDescription;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public List<TelemetryData.Location> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<TelemetryData.Location> waypoints) {
        this.waypoints = waypoints;
    }

    public List<String> getAssignedVehicles() {
        return assignedVehicles;
    }

    public void setAssignedVehicles(List<String> assignedVehicles) {
        this.assignedVehicles = assignedVehicles;
    }
}
