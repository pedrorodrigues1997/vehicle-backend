package com.example.vehicle_backend.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class TelemetryId implements Serializable {
    private String vin;
    private Instant timestamp;

    public TelemetryId() {}

    public TelemetryId(String vin, Instant timestamp) {
        this.vin = vin;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryId that = (TelemetryId) o;
        return Objects.equals(vin, that.vin) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp);
    }

    @Override
    public String toString() {
        return "TelemetryId{" +
                "vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
