package com.example.vehicle_backend.dto.RestRequests;

import com.example.vehicle_backend.entities.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RESTMissionRequest {
    @NotBlank(message = "Mission name is required")
    private String missionName;
    @NotBlank
    private String missionDescription;
    @NotBlank(message = "Goal is required")
    private String goal;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Valid
    private List<Location> waypoints;
    @NotEmpty(message = "Assigned vehicles list cannot be empty")
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

    public List<Location> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Location> waypoints) {
        this.waypoints = waypoints;
    }

    public List<String> getAssignedVehicles() {
        return assignedVehicles;
    }

    public void setAssignedVehicles(List<String> assignedVehicles) {
        this.assignedVehicles = assignedVehicles;
    }
}
