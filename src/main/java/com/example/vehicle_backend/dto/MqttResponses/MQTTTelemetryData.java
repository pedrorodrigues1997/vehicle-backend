package com.example.vehicle_backend.dto.MqttResponses;

import java.util.Objects;



public class MQTTTelemetryData {

    private String vin;
    private long timestamp;
    private double lat;
    private double lng;
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
