package com.example.vehicle_backend.entities.vehicleStatus;

import com.example.vehicle_backend.enums.HealthStatus;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@IdClass(VehicleStatusDataId.class)
@Table(name = "vehicle_health_status")
public class VehicleStatusData {

    @Id
    private String vehicleId;

    @Id
    private long timestamp;

    @Enumerated(EnumType.STRING)
    private HealthStatus engineStatus;
    private double engineOilLevelPercent;
    private boolean engineCheckEngineLight;

    @Enumerated(EnumType.STRING)
    private HealthStatus batteryStatus;
    private double batteryVoltage;

    private double tireFrontLeftPsi;
    private double tireFrontRightPsi;
    private double tireRearLeftPsi;
    private double tireRearRightPsi;

    @Enumerated(EnumType.STRING)
    private HealthStatus brakeStatus;


    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HealthStatus getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(HealthStatus engineStatus) {
        this.engineStatus = engineStatus;
    }

    public double getEngineOilLevelPercent() {
        return engineOilLevelPercent;
    }

    public void setEngineOilLevelPercent(double engineOilLevelPercent) {
        this.engineOilLevelPercent = engineOilLevelPercent;
    }

    public boolean isEngineCheckEngineLight() {
        return engineCheckEngineLight;
    }

    public void setEngineCheckEngineLight(boolean engineCheckEngineLight) {
        this.engineCheckEngineLight = engineCheckEngineLight;
    }

    public HealthStatus getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(HealthStatus batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getTireFrontLeftPsi() {
        return tireFrontLeftPsi;
    }

    public void setTireFrontLeftPsi(double tireFrontLeftPsi) {
        this.tireFrontLeftPsi = tireFrontLeftPsi;
    }

    public double getTireFrontRightPsi() {
        return tireFrontRightPsi;
    }

    public void setTireFrontRightPsi(double tireFrontRightPsi) {
        this.tireFrontRightPsi = tireFrontRightPsi;
    }

    public double getTireRearLeftPsi() {
        return tireRearLeftPsi;
    }

    public void setTireRearLeftPsi(double tireRearLeftPsi) {
        this.tireRearLeftPsi = tireRearLeftPsi;
    }

    public double getTireRearRightPsi() {
        return tireRearRightPsi;
    }

    public void setTireRearRightPsi(double tireRearRightPsi) {
        this.tireRearRightPsi = tireRearRightPsi;
    }

    public HealthStatus getBrakeStatus() {
        return brakeStatus;
    }

    public void setBrakeStatus(HealthStatus brakeStatus) {
        this.brakeStatus = brakeStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleStatusData that = (VehicleStatusData) o;
        return timestamp == that.timestamp && Double.compare(engineOilLevelPercent, that.engineOilLevelPercent) == 0 && engineCheckEngineLight == that.engineCheckEngineLight && Double.compare(batteryVoltage, that.batteryVoltage) == 0 && Double.compare(tireFrontLeftPsi, that.tireFrontLeftPsi) == 0 && Double.compare(tireFrontRightPsi, that.tireFrontRightPsi) == 0 && Double.compare(tireRearLeftPsi, that.tireRearLeftPsi) == 0 && Double.compare(tireRearRightPsi, that.tireRearRightPsi) == 0 && Objects.equals(vehicleId, that.vehicleId) && engineStatus == that.engineStatus && batteryStatus == that.batteryStatus && brakeStatus == that.brakeStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, timestamp, engineStatus, engineOilLevelPercent, engineCheckEngineLight, batteryStatus, batteryVoltage, tireFrontLeftPsi, tireFrontRightPsi, tireRearLeftPsi, tireRearRightPsi, brakeStatus);
    }

    @Override
    public String toString() {
        return "VehicleStatusData{" +
                "vehicleId='" + vehicleId + '\'' +
                ", timestamp=" + timestamp +
                ", engineStatus=" + engineStatus +
                ", engineOilLevelPercent=" + engineOilLevelPercent +
                ", engineCheckEngineLight=" + engineCheckEngineLight +
                ", batteryStatus=" + batteryStatus +
                ", batteryVoltage=" + batteryVoltage +
                ", tireFrontLeftPsi=" + tireFrontLeftPsi +
                ", tireFrontRightPsi=" + tireFrontRightPsi +
                ", tireRearLeftPsi=" + tireRearLeftPsi +
                ", tireRearRightPsi=" + tireRearRightPsi +
                ", brakeStatus=" + brakeStatus +
                '}';
    }
}