package com.example.vehicle_backend.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscriberService {

    private final String brokerUrl = "tcp://localhost:1883";
    private final String clientId = "backend-subscriber";

    private MqttClient client;


    public MqttSubscriberService() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("backendapp");
            options.setPassword("password123".toCharArray());
            //TODO Hide this. Also this user is not persisted in EMQX Built in database. Maybe use in mine?
            client.connect(options);

            client.subscribe("vehicles/+/telemetry", this::handleTelemetryMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void handleTelemetryMessage(String topic, MqttMessage message) {
        System.out.println("Received message on topic: " + topic);
        System.out.println("Payload: " + new String(message.getPayload()));
    }


}
