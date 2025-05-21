package com.example.vehicle_backend.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private static final String brokerUrl = "tcp://localhost:1883";
    private static final String clientId = "backend-subscriber";


    @Bean
    public MqttClient mqttClient() throws MqttException {

        MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setUserName("backendapp");
        options.setPassword("password123".toCharArray());
        client.connect(options);

        return client;
    }
}
