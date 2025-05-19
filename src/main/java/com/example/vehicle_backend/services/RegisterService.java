package com.example.vehicle_backend.services;

import com.example.vehicle_backend.dto.VehicleRegistrationRequest;
import com.example.vehicle_backend.model.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    private static final long ALLOWED_TIME_DIFF = 300;
    private static final Pattern VIN_PATTERN = Pattern.compile("^car-\\d{3}$");

    private final VehicleRepository vehicleRepository;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;


    public RegisterService(VehicleRepository vehicleRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }


    public void processRegister(String payload) throws SecurityException {

        VehicleRegistrationRequest req;
        try {
            req = objectMapper.readValue(payload, VehicleRegistrationRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON: " + e.getMessage(), e);
        }

        //Assuming that for this project VIN is always similar to car-00n
        if (isBlank(req.getVin()) || !VIN_PATTERN.matcher(req.getVin()).matches()) {
            throw new IllegalArgumentException("Invalid or missing VIN. Expected pattern: car-XXX");
        }

        if (isBlank(req.getModel())) {
            throw new IllegalArgumentException("Invalid or missing model.");
        }

        if (isBlank(req.getManufacturer())) {
            throw new IllegalArgumentException("Invalid or missing manufacturer.");
        }

        if (isBlank(req.getHardwareId())) {
            throw new IllegalArgumentException("Invalid or missing hardwareId.");
        }

        if (req.getTimestamp() == null) {
            throw new IllegalArgumentException("Missing timestamp.");
        }

        if (!isTimestampValid(req.getTimestamp())) {
            throw new IllegalArgumentException("Timestamp out of acceptable range.");
        }

        if (isBlank(req.getSecretToken()) || req.getSecretToken().length() < 8) {
            throw new IllegalArgumentException("Weak or missing secret token.");
        }


        Optional<Vehicle> existing = vehicleRepository.findByVin(req.getVin());
        if(existing.isPresent()){
            throw new IllegalArgumentException("Vehicle already registered with this VIN.");
        }


        String hashedToken = passwordEncoder.encode(req.getSecretToken());

        Vehicle vehicle = getVehicle(req, hashedToken);

        vehicleRepository.save(vehicle);
        System.out.println("Vehicle registered successfully: " + vehicle.getVin());

    }

    private boolean isTimestampValid(Long timestamp) {
        long now = Instant.now().getEpochSecond();
        return Math.abs(now - timestamp) <= ALLOWED_TIME_DIFF;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static Vehicle getVehicle(VehicleRegistrationRequest validatedReq, String hashedToken) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(validatedReq.getVin());
        vehicle.setModel(validatedReq.getModel());
        vehicle.setManufacturer(validatedReq.getManufacturer());
        vehicle.setMqttToken(hashedToken);
        vehicle.setHardwareId(validatedReq.getHardwareId());
        vehicle.setRegisteredAt(LocalDateTime.ofInstant(Instant.ofEpochSecond(validatedReq.getTimestamp()), ZoneId.systemDefault()));
        vehicle.setFirmwareVersion(validatedReq.getFirmwareVersion());
        vehicle.setBattery("100");
        return vehicle;
    }
}
