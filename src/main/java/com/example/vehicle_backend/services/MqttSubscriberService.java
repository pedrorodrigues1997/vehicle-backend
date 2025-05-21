package com.example.vehicle_backend.services;

import com.example.vehicle_backend.repositories.TelemetryDataRepository;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.topicHandlers.*;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscriberService {


    private static final String REGISTRATION_TOPIC = "vehicle/registration/request";
    private static final String TELEMETRY_TOPIC = "vehicles/+/telemetry";
    private static final String VEHICLE_STATUS_TOPIC = "vehicles/+/status";


    @Autowired
    private TelemetryHandler telemetryHandler;
    @Autowired
    private RegistrationHandler registerHandler;
    @Autowired
    private VehicleStatusHandler statusHandler;
    @Autowired
    private MqttClient mqttClient;


    @PostConstruct
    public void init() {
        try {

            mqttClient.subscribe(TELEMETRY_TOPIC, telemetryHandler::handle);
            mqttClient.subscribe(REGISTRATION_TOPIC, registerHandler::handle);
            mqttClient.subscribe(VEHICLE_STATUS_TOPIC, statusHandler::handle);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
