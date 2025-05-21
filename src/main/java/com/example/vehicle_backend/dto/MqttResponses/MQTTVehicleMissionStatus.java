package com.example.vehicle_backend.dto.MqttResponses;

import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.entities.Location;

import java.util.Objects;

public class MQTTVehicleMissionStatus {


    private String vin;
    private long missionId;
    private long timestamp;
    private MissionStatus status;
    private Location location;
    private int speed;


    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public long getMissionId() {
        return missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MQTTVehicleMissionStatus that = (MQTTVehicleMissionStatus) o;
        return timestamp == that.timestamp && speed == that.speed && Objects.equals(vin, that.vin) && Objects.equals(missionId, that.missionId) && status == that.status && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, missionId, timestamp, status, location, speed);
    }


    @Override
    public String toString() {
        return "VehicleMissionStatusPayload{" +
                "vin='" + vin + '\'' +
                ", missionId='" + missionId + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", location=" + location +
                ", speed=" + speed +
                '}';
    }
}
