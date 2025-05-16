package com.example.vehicle_backend.controller;


import com.example.vehicle_backend.dto.VehicleDTO;
import com.example.vehicle_backend.model.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Vehicle registerVehicle(@RequestBody VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(dto.getVin());
        vehicle.setModel(dto.getModel());
        vehicle.setManufacturer(dto.getManufacturer());
        vehicle.setOnMission(false);
        vehicle.setBattery("100");
        vehicle.setRegisteredAt(LocalDateTime.now());
        vehicle.setMqttToken(passwordEncoder.encode(dto.getSecretToken()));

        return vehicleRepository.save(vehicle);
    }
}
