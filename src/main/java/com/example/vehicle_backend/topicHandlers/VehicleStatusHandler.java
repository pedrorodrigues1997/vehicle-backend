package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTTelemetryData;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class VehicleStatusHandler implements MqttMessageHandler{



    private final VehicleRepository vehicleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public VehicleStatusHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public void handle(String topic, MqttMessage message) {
        // Topic format: vehicles/{vin}/status
        String[] topicParts = topic.split("/");
        if (topicParts.length != 3 || !"vehicles".equals(topicParts[0]) || !"status".equals(topicParts[2])) {
            System.out.println("Ignoring message from unexpected topic: " + topic);
            return;
        }

        String vinFromTopic = topicParts[1];


        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        if (payload.isBlank()) {
            System.out.println("Ignoring empty payload");
            return;
        }

        //MQTTTelemetryData status = objectMapper.readValue(payload, MQTTTelemetryData.class);

    }
}
