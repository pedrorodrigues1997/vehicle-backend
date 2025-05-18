package com.example.vehicle_backend.dto;

import jakarta.persistence.*;

import java.util.Objects;



public class TelemetryDataDTO {

    private String vin;
    private long timestamp;
    private double lat;
    private double lng;
    private double speed;
    private double engineTemp;
    private double fuelLevel;




    public String getVin() {
        return vin;
    }



    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryDataDTO that = (TelemetryDataDTO) o;
        return timestamp == that.timestamp && Double.compare(lat, that.lat) == 0 && Double.compare(lng, that.lng) == 0 && Double.compare(speed, that.speed) == 0 && Double.compare(engineTemp, that.engineTemp) == 0 && Double.compare(fuelLevel, that.fuelLevel) == 0 && Objects.equals(vin, that.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp, lat, lng, speed, engineTemp, fuelLevel);
    }

    @Override
    public String toString() {
        return "TelemetryDataDTO{" +
                "vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                ", lat=" + lat +
                ", lng=" + lng +
                ", speed=" + speed +
                ", engineTemp=" + engineTemp +
                ", fuelLevel=" + fuelLevel +
                '}';
    }
}
