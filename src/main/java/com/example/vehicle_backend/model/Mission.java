package com.example.vehicle_backend.model;


import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.persistence.*;

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

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @ElementCollection
    private List<String> assignedVehicles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "isActive")
    private boolean isActive;

    public Mission(Long missionId, String missionName, String missionDescription, String goal, List<TelemetryData.Location> waypoints, MissionStatus status, OffsetDateTime startTime, OffsetDateTime endTime, List<String> assignedVehicles, OffsetDateTime createdAt, OffsetDateTime updatedAt, boolean isActive) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.missionDescription = missionDescription;
        this.goal = goal;
        this.waypoints = waypoints;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.assignedVehicles = assignedVehicles;
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

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getAssignedVehicles() {
        return assignedVehicles;
    }

    public void setAssignedVehicles(List<String> assignedVehicles) {
        this.assignedVehicles = assignedVehicles;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return isActive == mission.isActive && Objects.equals(missionId, mission.missionId) && Objects.equals(missionName, mission.missionName) && Objects.equals(missionDescription, mission.missionDescription) && Objects.equals(goal, mission.goal) && Objects.equals(waypoints, mission.waypoints) && status == mission.status && Objects.equals(startTime, mission.startTime) && Objects.equals(endTime, mission.endTime) && Objects.equals(assignedVehicles, mission.assignedVehicles) && Objects.equals(createdAt, mission.createdAt) && Objects.equals(updatedAt, mission.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, missionName, missionDescription, goal, waypoints, status, startTime, endTime, assignedVehicles, createdAt, updatedAt, isActive);
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
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", assignedVehicles=" + assignedVehicles +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                '}';
    }
}
