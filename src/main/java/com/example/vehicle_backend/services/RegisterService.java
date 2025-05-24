package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.MqttResponses.MQTTVehicleRegistrationRequest;
import com.example.vehicle_backend.entities.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    private final VehicleRepository vehicleRepository;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;


    public RegisterService(VehicleRepository vehicleRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }


    public void processRegister(String payload) throws SecurityException {

        MQTTVehicleRegistrationRequest req;
        try {
            req = objectMapper.readValue(payload, MQTTVehicleRegistrationRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON: " + e.getMessage(), e);
        }

        CommonDataValidator.validate(req);

        if (isValidRegisterRequest(req)) {
            String hashedToken = passwordEncoder.encode(req.getSecretToken());
            Vehicle vehicle = getVehicle(req, hashedToken);

            vehicleRepository.save(vehicle);
            System.out.println("Vehicle registered successfully: " + vehicle.getVin());

        }
    }

    private boolean isValidRegisterRequest(MQTTVehicleRegistrationRequest req) {
        CommonDataValidator.validateVIN(req.getVin());
        CommonDataValidator.validateTimestamp(req.getTimestamp());
        CommonDataValidator.validatePassword(req.getSecretToken());

        Optional<Vehicle> existing = vehicleRepository.findByVin(req.getVin());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Vehicle already registered with this VIN.");
        }

        return true;
    }

    private static Vehicle getVehicle(MQTTVehicleRegistrationRequest validatedReq, String hashedToken) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(validatedReq.getVin());
        vehicle.setModel(validatedReq.getModel());
        vehicle.setManufacturer(validatedReq.getManufacturer());
        vehicle.setMqttToken(hashedToken);
        vehicle.setHardwareId(validatedReq.getHardwareId());
        vehicle.setRegisteredAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(validatedReq.getTimestamp()), ZoneId.systemDefault()));
        vehicle.setFirmwareVersion(validatedReq.getFirmwareVersion());
        return vehicle;
    }
}
