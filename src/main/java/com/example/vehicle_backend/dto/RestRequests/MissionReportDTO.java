package com.example.vehicle_backend.dto.RestRequests;

import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.entities.Vehicle;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class MissionReportDTO {
    @NotNull(message = "Mission ID is required")
    private Long missionId;
    @NotBlank(message = "Mission name is required")
    private String missionName;
    @NotBlank(message = "Mission description is required")
    private String missionDescription;
    @NotBlank(message = "Goal is required")
    private String goal;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Valid
    private List<Location> waypoints;
    @NotNull(message = "Status is required")
    private MissionStatus status;

    @NotEmpty(message = "Vehicle data list cannot be empty")
    @Valid
    private List<@Valid VehicleMissionDataDTO> vehicleMissionDataList;
    @NotNull(message = "Creation timestamp is required")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endTime;
    private boolean isActive;

    public MissionReportDTO(Mission mission, List<VehicleMissionDataDTO> vmdList) {
        this.missionId = mission.getMissionId();
        this.missionName = mission.getMissionName();
        this.missionDescription = mission.getMissionDescription();
        this.goal = mission.getGoal();
        this.waypoints = mission.getWaypoints();
        this.status = mission.getStatus();
        this.endTime = mission.getEndTime();
        this.vehicleMissionDataList = vmdList;
        this.createdAt = mission.getCreatedAt();
        this.updatedAt = mission.getUpdatedAt();
        this.isActive = mission.isActive();
    }


    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

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

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<VehicleMissionDataDTO> getVehicleMissionDataList() {
        return vehicleMissionDataList;
    }

    public void setVehicleMissionDataList(List<VehicleMissionDataDTO> vehicleMissionDataList) {
        this.vehicleMissionDataList = vehicleMissionDataList;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}


