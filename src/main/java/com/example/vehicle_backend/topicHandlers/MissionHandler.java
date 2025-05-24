package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTVehicleMissionStatus;
import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

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
            failMissionDueToError();
            e.printStackTrace();
        }
    }


    public boolean handleVehicleResponse(String vehicleId, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            MQTTVehicleMissionStatus statusPayload = objectMapper.readValue(payload, MQTTVehicleMissionStatus.class);
            System.out.println("[MissionHandler] Response from " + vehicleId + ": " + statusPayload);


            Mission mission = missionRepository.findById(statusPayload.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + statusPayload.getMissionId()));

            CommonDataValidator.validate(statusPayload);
            validateVehicleStatusPayload(statusPayload, mission);

            //We should only receive data from or about vehicles that are assigned to the mission
            VehicleMissionData vehicleData = mission.getVehicleMissionDataList().stream()
                    .filter(v -> v.getVehicleId().equals(vehicleId))
                    .filter(v -> v.getVehicleId().equals(statusPayload.getVin()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Message from vehicle " + vehicleId + " fails. Either the vehicle is not assigned to mission " + mission.getMissionId() + " or the information in the payload is not about him."));

            CommonDataValidator.validateStatusProgression(vehicleData.getStatus(), statusPayload.getStatus());


            vehicleData.setStatus(statusPayload.getStatus());
            vehicleData.setLastUpdateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(statusPayload.getTimestamp()), ZoneId.systemDefault()));
            vehicleData.setSpeed(statusPayload.getSpeed());
            vehicleData.setLocation(statusPayload.getLocation());


            mission.setUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));


            updateOverallMissionStatus(mission);
            if (mission.getStatus() == MissionStatus.COMPLETED ||
                    mission.getStatus() == MissionStatus.FAILED ||
                    mission.getStatus() == MissionStatus.CANCELLED) {

                mission.setEndTime(LocalDateTime.now(ZoneId.systemDefault()));
                mission.setActive(false);
            }

            missionRepository.save(mission);


        } catch (Exception e) {
            System.err.println("[MissionHandler] Error processing vehicle response: " + e.getMessage());
            failMissionDueToError();
            e.printStackTrace();
        }

        return mission.isActive();  //If the data is not valid somewhere im assuming the mission failed
    }

    private static void validateVehicleStatusPayload(MQTTVehicleMissionStatus statusPayload, Mission mission) {
        CommonDataValidator.validateVIN(statusPayload.getVin());
        CommonDataValidator.validateTimestamp(statusPayload.getTimestamp());
        if (statusPayload.getMissionId() == 0) {
            throw new IllegalArgumentException("Invalid or missing missionId.");
        }
    }

    /**
     * Return value true means that the mission keeps ongoing
     * Return false means that it ended
     *
     * @param mission
     * @return
     */
    private static void updateOverallMissionStatus(Mission mission) {
        List<VehicleMissionData> vehicleDataList = mission.getVehicleMissionDataList();

        if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.COMPLETED)) {
            mission.setStatus(MissionStatus.COMPLETED);
        } else if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.CANCELLED)) {
            mission.setStatus(MissionStatus.CANCELLED);
        } else if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.FAILED)) {
            mission.setStatus(MissionStatus.FAILED);
        } else if (vehicleDataList.stream().allMatch(v -> v.getStatus() == MissionStatus.PENDING)) {
            mission.setStatus(MissionStatus.PENDING);
        } else {
            mission.setStatus(MissionStatus.IN_PROGRESS);
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
        for (Location wp : mission.getWaypoints()) {
            ObjectNode wpNode = waypointsArray.addObject();
            wpNode.put("latitude", wp.getLat());
            wpNode.put("longitude", wp.getLng());
        }

        return mapper.writeValueAsString(root);
    }

    private void failMissionDueToError() {
        mission.setActive(false);
        mission.setStatus(MissionStatus.FAILED);
        missionRepository.save(mission);
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
