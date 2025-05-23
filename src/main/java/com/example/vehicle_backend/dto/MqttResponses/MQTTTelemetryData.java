package com.example.vehicle_backend.dto.MqttResponses;

import jakarta.validation.constraints.*;

import java.util.Objects;



public class MQTTTelemetryData {

    @NotBlank(message = "vehicleId must not be blank")
    private String vin;

    @Positive(message = "timestamp must be a positive value represented in millis")
    private long timestamp;

    @Min(value = -90, message = "Latitude must be >= -90")
    @Max(value = 90, message = "Latitude must be <= 90")
    private double lat;

    @Min(value = -90, message = "Longitude must be >= -180")
    @Max(value = 90, message = "Longitude must be <= 180")
    private double lng;

    @PositiveOrZero(message = "Speed must be >= 0")
    private double speed;


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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MQTTTelemetryData that = (MQTTTelemetryData) o;
        return timestamp == that.timestamp && Double.compare(lat, that.lat) == 0 && Double.compare(lng, that.lng) == 0 && Double.compare(speed, that.speed) == 0 && Objects.equals(vin, that.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, timestamp, lat, lng, speed);
    }

    @Override
    public String toString() {
        return "TelemetryDataDTO{" +
                "vin='" + vin + '\'' +
                ", timestamp=" + timestamp +
                ", lat=" + lat +
                ", lng=" + lng +
                ", speed=" + speed +
                '}';
    }
}
