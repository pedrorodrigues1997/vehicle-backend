package com.example.vehicle_backend.services;

import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.model.Mission;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.example.vehicle_backend.topicHandlers.MissionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class MissionManager {

    private final Map<Long, MissionHandler> activeMissions = new ConcurrentHashMap<>();
    private final MissionRepository missionRepository;
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;


    public MissionManager(MissionRepository missionRepository, MqttClient mqttClient, ObjectMapper objectMapper) {
        this.missionRepository = missionRepository;
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws MqttException {
        mqttClient.subscribe("api/mission/+/vehicles/+", (topic, message) -> {
            handleIncomingMessage(topic, message);
        });
    }


    private void handleIncomingMessage(String topic, MqttMessage message) {
        // Example topic: api/mission/abc123/vehicles/veh45
        String[] parts = topic.split("/");
        if (parts.length < 5) return;

        Long missionId = Long.parseLong(parts[2]);
        String vehicleId = parts[4];

        MissionHandler handler = activeMissions.get(missionId);
        if (handler != null) {
            if(handler.handleVehicleResponse(vehicleId, message)){
                unregisterMissionHandler(missionId);
            }
        } else {
            System.out.println("No handler found for mission: " + missionId);
        }
    }


    public void registerMissionHandler(Long missionId, Mission mission) {
        MissionHandler handler = new MissionHandler(mission, missionRepository, mqttClient, objectMapper);
        activeMissions.put(missionId, handler);
    }

    public void unregisterMissionHandler(Long missionId) {
        activeMissions.remove(missionId);
    }


    public boolean startMission(Long missionId) {
        MissionHandler handler = activeMissions.get(missionId);
        if (handler != null) {
            handler.sendStartCommand();
            return true;
        }
        return false;
    }

}
