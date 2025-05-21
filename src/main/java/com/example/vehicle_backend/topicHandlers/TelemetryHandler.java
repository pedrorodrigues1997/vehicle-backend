package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTTelemetryData;
import com.example.vehicle_backend.services.TelemetryService;
import com.example.vehicle_backend.validators.CommonDataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TelemetryHandler implements MqttMessageHandler {


    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TelemetryHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public void handle(String topic, MqttMessage message) {
        try {

            // Topic format: vehicles/{vin}/telemetry
            String[] topicParts = topic.split("/");
            if (topicParts.length != 3 || !"vehicles".equals(topicParts[0]) || !"telemetry".equals(topicParts[2])) {
                System.out.println("Ignoring message from unexpected topic: " + topic);
                return;
            }

            String vinFromTopic = topicParts[1];


            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            if (payload.isBlank()) {
                System.out.println("Ignoring empty payload");
                return;
            }

            MQTTTelemetryData telemetryDTO = objectMapper.readValue(payload, MQTTTelemetryData.class);

            CommonDataValidator.validateVIN(telemetryDTO.getVin());

            if (!vinFromTopic.equals(telemetryDTO.getVin())) {
                System.out.printf("VIN mismatch: topic VIN '%s' vs payload VIN '%s'%n", vinFromTopic, telemetryDTO.getVin());
                return;
            }

            telemetryService.processTelemetry(telemetryDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
