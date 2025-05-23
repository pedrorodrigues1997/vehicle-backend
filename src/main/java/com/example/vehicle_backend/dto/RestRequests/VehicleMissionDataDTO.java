package com.example.vehicle_backend.dto.RestRequests;

import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.Vehicle;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class VehicleMissionDataDTO {
    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;
    @NotNull(message = "Status is required")
    private MissionStatus status;
    private LocalDateTime lastUpdateTime;
    @NotNull(message = "Location is required")
    @Valid
    private Location location;
    @Min(value = 0, message = "Speed must be zero or positive")
    private int speed;
    @NotNull(message = "Vehicle data is required")
    @Valid
    private VehicleDTO vehicle;

    public VehicleMissionDataDTO(VehicleMissionData vmd, Vehicle vehicle) {
        this.vehicleId = vmd.getVehicleId();
        this.status = vmd.getStatus();
        this.lastUpdateTime = vmd.getLastUpdateTime();
        this.location = vmd.getLocation();
        this.speed = vmd.getSpeed();
        this.vehicle = new VehicleDTO(vehicle);
    }


    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }
}
