package com.example.vehicle_backend.dto.MqttResponses;

import java.util.Objects;


public class MQTTStatusData {

    private String vin;
    private long timestamp;
    private double oilLevel;
    private double engineTemp;
    private double fuelLevel;
    private double vehicleHealth;
    private double outsideTemp;


    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getOilLevel() {
        return oilLevel;
    }

    public void setOilLevel(double oilLevel) {
        this.oilLevel = oilLevel;
    }

    public double getEngineTemp() {
        return engineTemp;
    }

    public void setEngineTemp(double engineTemp) {
        this.engineTemp = engineTemp;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public double getVehicleHealth() {
        return vehicleHealth;
    }

    public void setVehicleHealth(double vehicleHealth) {
        this.vehicleHealth = vehicleHealth;
    }

    public double getOutsideTemp() {
        return outsideTemp;
    }

    public void setOutsideTemp(double outsideTemp) {
        this.outsideTemp = outsideTemp;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MQTTStatusData that = (MQTTStatusData) o;
        return timestamp == that.timestamp && Double.compare(oilLevel, that.oilLevel) == 0 && Double.compare(engineTemp, that.engineTemp) == 0 && Double.compare(fuelLevel, that.fuelLevel) == 0 && Double.compare(vehicleHealth, that.vehicleHealth) == 0 && Double.compare(outsideTemp, that.outsideTemp) == 0 && Objects.equals(vin, that.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp, oilLevel, engineTemp, fuelLevel, vehicleHealth, outsideTemp);
    }


    @Override
    public String toString() {
        return "MQTTStatusData{" +
                "vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                ", oilLevel=" + oilLevel +
                ", engineTemp=" + engineTemp +
                ", fuelLevel=" + fuelLevel +
                ", vehicleHealth=" + vehicleHealth +
                ", outsideTemp=" + outsideTemp +
                '}';
    }
}
