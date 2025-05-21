package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.services.RegisterService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

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
}
