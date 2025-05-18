package com.example.vehicle_backend.topicHandlers;

import org.eclipse.paho.client.mqttv3.MqttMessage;

@FunctionalInterface
public interface MqttMessageHandler {
    void handle(String topic, MqttMessage message);
}
