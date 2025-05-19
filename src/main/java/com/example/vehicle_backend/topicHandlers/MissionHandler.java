package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.VehicleMissionStatusPayload;
import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.model.Mission;
import com.example.vehicle_backend.model.TelemetryData;
import com.example.vehicle_backend.model.VehicleMissionData;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MissionHandler {


    private final Mission mission;
    private final MissionRepository missionRepository;
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    public MissionHandler(Mission mission, MissionRepository missionRepository, MqttClient mqttClient, ObjectMapper objectMapper) {
        this.mission = mission;
        this.missionRepository = missionRepository;
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
    }


    public void sendStartCommand() {
        try {


            for (VehicleMissionData vmd : mission.getVehicleMissionDataList()) {
                String vin = vmd.getVehicleId();
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


    public boolean handleVehicleResponse(String vehicleId, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            System.out.println("[MissionHandler] Response from " + vehicleId + ": " + payload);
            VehicleMissionStatusPayload statusPayload = objectMapper.readValue(payload, VehicleMissionStatusPayload.class);


            if (statusPayload.getVin() == null || !statusPayload.getVin().equals(vehicleId)) {
                throw new IllegalArgumentException("Missing or invalid vin");
            }
            Mission mission = missionRepository.findById(statusPayload.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + statusPayload.getMissionId()));

            //We should only receive data from vehicles that are assigned to the mission
            VehicleMissionData vehicleData = mission.getVehicleMissionDataList().stream()
                    .filter(v -> v.getVehicleId().equals(vehicleId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Vehicle " + vehicleId + " is not assigned to mission " + mission.getMissionId()));

            //TODO Validate statusPayload first


            vehicleData.setStatus(statusPayload.getStatus());
            vehicleData.setLastUpdateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(statusPayload.getTimestamp()), ZoneId.systemDefault()));
            vehicleData.setSpeed(statusPayload.getSpeed());
            vehicleData.setLocation(statusPayload.getLocation());


            mission.setUpdatedAt(LocalDateTime.ofInstant(Instant.ofEpochSecond(statusPayload.getTimestamp()), ZoneId.systemDefault()));


            boolean isFinished = updateOverallMissionStatus(mission);
            if(isFinished){
                mission.setEndTime(LocalDateTime.now(ZoneId.systemDefault()));
                mission.setActive(false);
            }
            missionRepository.save(mission);
            return isFinished;



        } catch (Exception e) {
            System.err.println("[MissionHandler] Error processing vehicle response: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Return value true means that the mission keeps ongoing
     * Return false means that it ended
     * @param mission
     * @return
     */
    private static boolean updateOverallMissionStatus(Mission mission) {
        List<VehicleMissionData> vehicleDataList = mission.getVehicleMissionDataList();

        if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.COMPLETED)) {
            mission.setStatus(MissionStatus.COMPLETED);
            return true;
        } else if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.CANCELLED)) {
            mission.setStatus(MissionStatus.CANCELLED);
            return true;
        } else if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.FAILED)) {
            mission.setStatus(MissionStatus.FAILED);
            return true;
        } else if (vehicleDataList.stream().anyMatch(v -> v.getStatus() == MissionStatus.IN_PROGRESS)) {
            mission.setStatus(MissionStatus.IN_PROGRESS);
        } else if (vehicleDataList.stream().anyMatch(v -> v.getStatus() == MissionStatus.PENDING)) {
            mission.setStatus(MissionStatus.PENDING);
        }else{
            mission.setStatus(MissionStatus.IN_PROGRESS);
        }
        return false;
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
