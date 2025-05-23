package com.example.vehicle_backend.dto.RestRequests;

import com.example.vehicle_backend.entities.Vehicle;
import jakarta.validation.constraints.NotBlank;

public class VehicleDTO {
    @NotBlank(message = "VIN is required")
    private String vin;
    @NotBlank(message = "Model is required")
    private String model;
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;
    private String firmwareVersion;
    private String hardwareId;

    public VehicleDTO(Vehicle vehicle) {
        this.vin = vehicle.getVin();
        this.model = vehicle.getModel();
        this.manufacturer = vehicle.getManufacturer();
        this.firmwareVersion = vehicle.getFirmwareVersion();
        this.hardwareId = vehicle.getHardwareId();
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
}
