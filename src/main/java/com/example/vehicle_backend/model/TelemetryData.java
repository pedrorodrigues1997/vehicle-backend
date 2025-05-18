package com.example.vehicle_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@IdClass(TelemetryId.class)
@Entity
@Table(name = "vehicle_telemetry")
public class TelemetryData {


    @Id
    private String vin;
    @Id
    private Instant timestamp;
    //Id is composed of vin and timestamp

    @Embedded
    private Location location;
    private double speed;
    private double engineTemp;
    private double fuelLevel;


    @Embeddable
    public static class Location {

        @Min(-90)
        @Max(90)
        private double lat;

        @Min(-180)
        @Max(180)
        private double lng;

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
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        TelemetryData that = (TelemetryData) o;
        return Double.compare(speed, that.speed) == 0 && Double.compare(engineTemp, that.engineTemp) == 0 && Double.compare(fuelLevel, that.fuelLevel) == 0 && Objects.equals(vin, that.vin) && Objects.equals(timestamp, that.timestamp) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp, location, speed, engineTemp, fuelLevel);
    }

    @Override
    public String toString() {
        return "TelemetryData{" +
                ", vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                ", location=" + location +
                ", speed=" + speed +
                ", engineTemp=" + engineTemp +
                ", fuelLevel=" + fuelLevel +
                '}';
    }
}



