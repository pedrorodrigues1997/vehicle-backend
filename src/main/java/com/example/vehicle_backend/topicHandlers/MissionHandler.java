package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.model.Mission;
import com.example.vehicle_backend.model.TelemetryData;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MissionHandler {


    private final Mission mission;
    private final MissionRepository missionRepository;
    private final MqttClient mqttClient;

    public MissionHandler(Mission mission, MissionRepository missionRepository, MqttClient mqttClient) {
        this.mission = mission;
        this.missionRepository = missionRepository;
        this.mqttClient = mqttClient;
    }


    public void sendStartCommand() {
        try {


            for (String vin : mission.getAssignedVehicles()) {
                String payload = generateStartPayload(mission.getMissionId(), vin);
                String topic = "api/requests/mission/" + mission.getMissionId() + "/vehicles/" + vin;

                mqttClient.publish(topic, new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));
                System.out.println("[MissionHandler] Sent start mission command for VIN " + vin + " to topic: " + topic);
            }
            mission.setActive(true);
            missionRepository.save(mission);

        } catch (Exception e) {
            System.err.println("[MissionHandler] Failed to send start command: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void handleVehicleResponse(String vehicleId, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            System.out.println("[MissionHandler] Response from " + vehicleId + ": " + payload);

            // Parse status update (you can replace with real JSON deserialization)
            //VehicleStatusUpdate update = parseVehicleStatus(vehicleId, payload);
            //vehicleStatuses.put(vehicleId, update);

            //evaluateMissionProgress();

        } catch (Exception e) {
            System.err.println("[MissionHandler] Error processing vehicle response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateStartPayload(Long missionId, String vin) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("missionId", missionId.toString());
        root.put("vin", vin);
        root.put("command", "start");

        ObjectNode missionData = root.putObject("missionData");
        missionData.put("missionName", mission.getMissionName());
        missionData.put("missionDescription", mission.getMissionDescription());
        missionData.put("goal", mission.getGoal());

        ArrayNode waypointsArray = missionData.putArray("waypoints");
        for (TelemetryData.Location wp : mission.getWaypoints()) {
            ObjectNode wpNode = waypointsArray.addObject();
            wpNode.put("latitude", wp.getLat());
            wpNode.put("longitude", wp.getLng());
        }

        return mapper.writeValueAsString(root);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MissionHandler that = (MissionHandler) o;
        return Objects.equals(mission, that.mission) && Objects.equals(missionRepository, that.missionRepository) && Objects.equals(mqttClient, that.mqttClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mission, missionRepository, mqttClient);
    }
}
