package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.repositories.VehicleRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class VehicleStatusHandler implements MqttMessageHandler{



    private final VehicleRepository vehicleRepository;

    public VehicleStatusHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public void handle(String topic, MqttMessage message) {

    }
}
