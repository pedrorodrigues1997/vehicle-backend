package com.example.vehicle_backend.dto.MqttResponses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class MQTTVehicleRegistrationRequest {

    @NotBlank
    private String vin;

    @NotBlank
    private String model;

    @NotBlank
    private String manufacturer;

    @NotBlank
    private String firmwareVersion;

    @NotBlank
    private String hardwareId;

    @NotBlank
    private String secretToken;

    @NotNull
    private Long timestamp;


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

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MQTTVehicleRegistrationRequest that = (MQTTVehicleRegistrationRequest) o;
        return Objects.equals(vin, that.vin) && Objects.equals(model, that.model) && Objects.equals(manufacturer, that.manufacturer) && Objects.equals(firmwareVersion, that.firmwareVersion) && Objects.equals(hardwareId, that.hardwareId) && Objects.equals(secretToken, that.secretToken) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, model, manufacturer, firmwareVersion, hardwareId, secretToken, timestamp);
    }

    @Override
    public String toString() {
        return "VehicleRegistrationRequest{" +
                "vin='" + vin + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", hardwareId='" + hardwareId + '\'' +
                ", secretToken='" + secretToken + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
