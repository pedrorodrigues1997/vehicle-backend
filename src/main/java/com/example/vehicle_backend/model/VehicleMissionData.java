package com.example.vehicle_backend.model;


import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class VehicleMissionData {

    private String vehicleId;

    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    private LocalDateTime lastUpdateTime;

    @Embedded
    private TelemetryData.Location location;

    private int speed;


    public VehicleMissionData() {}

    public VehicleMissionData(String vehicleId) {
        this.vehicleId = vehicleId;
        this.status = MissionStatus.PENDING;
        this.lastUpdateTime = LocalDateTime.now();
    }



    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public TelemetryData.Location getLocation() {
        return location;
    }

    public void setLocation(TelemetryData.Location location) {
        this.location = location;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleMissionData that = (VehicleMissionData) o;
        return speed == that.speed && Objects.equals(vehicleId, that.vehicleId) && status == that.status && Objects.equals(lastUpdateTime, that.lastUpdateTime) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, status, lastUpdateTime, location, speed);
    }

    @Override
    public String toString() {
        return "VehicleMissionData{" +
                ", vehicleId='" + vehicleId + '\'' +
                ", status=" + status +
                ", lastUpdateTime=" + lastUpdateTime +
                ", location=" + location +
                ", speed=" + speed +
                '}';
    }
}
