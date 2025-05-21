package com.example.vehicle_backend.entities;

import jakarta.persistence.*;

import java.time.Instant;
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryData that = (TelemetryData) o;
        return Double.compare(speed, that.speed) == 0 && Objects.equals(vin, that.vin) && Objects.equals(timestamp, that.timestamp) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp, location, speed);
    }

    @Override
    public String toString() {
        return "TelemetryData{" +
                "vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                ", location=" + location +
                ", speed=" + speed +
                '}';
    }
}



