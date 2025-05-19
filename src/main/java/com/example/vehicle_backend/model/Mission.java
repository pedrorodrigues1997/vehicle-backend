package com.example.vehicle_backend.model;


import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "missions")
public class Mission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionId;

    @Column(nullable = false)
    private String missionName;

    @Column(nullable = false)
    private String missionDescription;

    @Column(nullable = false)
    private String goal;

    @ElementCollection
    @CollectionTable(name = "mission_waypoints", joinColumns = @JoinColumn(name = "mission_id"))
    private List<TelemetryData.Location> waypoints = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mission_vehicle_data", joinColumns = @JoinColumn(name = "mission_id"))
    private List<VehicleMissionData> vehicleMissionDataList = new ArrayList<>();


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "isActive")
    private boolean isActive;

    public Mission(Long missionId, String missionName, String missionDescription, String goal, List<TelemetryData.Location> waypoints, MissionStatus status, LocalDateTime endTime, List<VehicleMissionData> vehicleMissionDataList, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.missionDescription = missionDescription;
        this.goal = goal;
        this.waypoints = waypoints;
        this.status = status;
        this.endTime = endTime;
        this.vehicleMissionDataList = vehicleMissionDataList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Mission() {

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

    public String getMissionDescription() {
        return missionDescription;
    }

    public void setMissionDescription(String missionDescription) {
        this.missionDescription = missionDescription;
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


    public List<VehicleMissionData> getVehicleMissionDataList() {
        return vehicleMissionDataList;
    }

    public void setVehicleMissionDataList(List<VehicleMissionData> vehicleMissionDataList) {
        this.vehicleMissionDataList = vehicleMissionDataList;
    }

    public void addVehicleMissionData(List<String> vehicleIds) {
        for (String vin : vehicleIds) {
            VehicleMissionData vmd = new VehicleMissionData();
            vmd.setVehicleId(vin);
            vmd.setStatus(MissionStatus.PENDING);
            this.vehicleMissionDataList.add(vmd);
        }
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return isActive == mission.isActive && Objects.equals(missionId, mission.missionId) && Objects.equals(missionName, mission.missionName) && Objects.equals(missionDescription, mission.missionDescription) && Objects.equals(goal, mission.goal) && Objects.equals(waypoints, mission.waypoints) && status == mission.status && Objects.equals(endTime, mission.endTime) && Objects.equals(vehicleMissionDataList, mission.vehicleMissionDataList) && Objects.equals(createdAt, mission.createdAt) && Objects.equals(updatedAt, mission.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, missionName, missionDescription, goal, waypoints, status, endTime, vehicleMissionDataList, createdAt, updatedAt, isActive);
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionId=" + missionId +
                ", missionName='" + missionName + '\'' +
                ", missionDescription='" + missionDescription + '\'' +
                ", goal='" + goal + '\'' +
                ", waypoints=" + waypoints +
                ", status=" + status +
                ", endTime=" + endTime +
                ", vehicleMissionDataList=" + vehicleMissionDataList +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                '}';
    }
}
