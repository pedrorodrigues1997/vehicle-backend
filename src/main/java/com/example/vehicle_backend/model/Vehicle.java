package com.example.vehicle_backend.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Vehicle {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String vin;

    private String model;
    private String manufacturer;
    private String battery;
    private boolean isOnMission;
    private LocalDateTime registeredAt;

    private String mqttToken; //For authentication


    public Long getId() {
        return id;
    }


    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public boolean isOnMission() {
        return isOnMission;
    }

    public void setOnMission(boolean onMission) {
        isOnMission = onMission;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getMqttToken() {
        return mqttToken;
    }

    public void setMqttToken(String mqttToken) {
        this.mqttToken = mqttToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return isOnMission == vehicle.isOnMission && Objects.equals(id, vehicle.id) && Objects.equals(vin, vehicle.vin) && Objects.equals(model, vehicle.model) && Objects.equals(manufacturer, vehicle.manufacturer) && Objects.equals(battery, vehicle.battery) && Objects.equals(registeredAt, vehicle.registeredAt) && Objects.equals(mqttToken, vehicle.mqttToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vin, model, manufacturer, battery, isOnMission, registeredAt, mqttToken);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", vin='" + vin + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", battery='" + battery + '\'' +
                ", isOnMission=" + isOnMission +
                ", registeredAt=" + registeredAt +
                ", mqttToken='" + mqttToken + '\'' +
                '}';
    }
}
