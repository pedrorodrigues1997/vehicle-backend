package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.VehicleRegistrationRequest;
import com.example.vehicle_backend.model.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.services.RegisterService;
import com.example.vehicle_backend.services.TelemetryService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class RegistrationHandler implements MqttMessageHandler{

    private final RegisterService registerService;

    public RegistrationHandler(RegisterService registerService) {
        this.registerService = registerService;
    }


    @Override
    public void handle(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Registration request received: " + payload);
        try {
            registerService.processRegister(payload);
        }catch (IllegalArgumentException e){
            System.out.println("Vehicle failed to register with cause: " + e.getMessage());
        }

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
