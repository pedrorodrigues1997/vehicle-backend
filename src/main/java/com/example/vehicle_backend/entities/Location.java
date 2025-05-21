package com.example.vehicle_backend.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;

@Embeddable
public class Location {

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private double lat;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
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
