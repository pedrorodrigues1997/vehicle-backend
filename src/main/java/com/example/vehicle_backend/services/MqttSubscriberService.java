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
    private MqttClient mqttClient;

    public MqttSubscriberService() {
    }

    @PostConstruct
    public void init() {
        try {

           // subscribeWithHandler(REGISTRATION_TOPIC, new RegistrationHandler(vehicleRepository));
            mqttClient.subscribe(TELEMETRY_TOPIC, telemetryHandler::handle);          //  subscribeWithHandler(VEHICLE_STATUS_TOPIC, new VehicleStatusHandler(vehicleRepository));
           // subscribeWithHandler(MISSION_TOPIC, new MissionHandler(vehicleRepository));


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
