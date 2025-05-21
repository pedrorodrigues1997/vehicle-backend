package com.example.vehicle_backend.entities.vehicleStatus;

import java.io.Serializable;
import java.util.Objects;

public class VehicleStatusDataId implements Serializable {

    private String vehicleId;
    private long timestamp;

    public VehicleStatusDataId() {}

    public VehicleStatusDataId(String vehicleId, long timestamp) {
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleStatusDataId)) return false;
        VehicleStatusDataId that = (VehicleStatusDataId) o;
        return Objects.equals(vehicleId, that.vehicleId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, timestamp);
    }


    @Override
    public String toString() {
        return "VehicleStatusDataId{" +
                "vehicleId='" + vehicleId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}