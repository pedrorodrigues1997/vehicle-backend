package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.repositories.VehicleRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RegistrationHandler implements MqttMessageHandler{

    private final VehicleRepository vehicleRepository;

    public RegistrationHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public void handle(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Registration request received: " + payload);

        // Parse payload
        // TODO: Validate/store vehicle, generate credentials

        // Simulate sending credentials back
        String vehicleId = extractVehicleId(payload); // implement this
        String responseTopic = "vehicle/registration/response/" + vehicleId;
        String responsePayload = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", vehicleId, "testpass");


    }

    private String extractVehicleId(String payload) {
        // Basic string parsing or use a JSON library like Jackson
        // Here, assuming payload contains "vehicle_id": "veh-123"
        int idx = payload.indexOf("vehicle_id");
        if (idx == -1) return "unknown";
        int start = payload.indexOf("\"", idx + 12) + 1;
        int end = payload.indexOf("\"", start);
        return payload.substring(start, end);
    }


}
