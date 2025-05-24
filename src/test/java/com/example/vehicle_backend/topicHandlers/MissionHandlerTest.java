package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTVehicleMissionStatus;
import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.enums.MissionStatus;
import com.example.vehicle_backend.repositories.MissionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MissionHandlerTest {



    private MissionRepository missionRepository;
    private MqttClient mqttClient;
    private ObjectMapper objectMapper;
    private Mission mission;
    private MissionHandler missionHandler;





    @BeforeEach
    void setup() {
        missionRepository = Mockito.mock(MissionRepository.class);
        mqttClient = Mockito.mock(MqttClient.class);
        objectMapper = new ObjectMapper();

        VehicleMissionData vmd = new VehicleMissionData();
        vmd.setVehicleId("car-001");
        vmd.setStatus(MissionStatus.PENDING);


        Location location = new Location();
        location.setLat(41.0);
        location.setLng(-7.0);

        mission = new Mission();
        mission.setMissionId(123L);
        mission.setMissionName("Test Mission");
        mission.setMissionDescription("Test Desc");
        mission.setGoal("Test Goal");
        mission.addVehicleMissionData(List.of("car-001", "car-002"));
        mission.setWaypoints(List.of(location));

        missionHandler = new MissionHandler(mission, missionRepository, mqttClient, objectMapper);
    }

    @Test
    void testSendStartCommandPublishesMessageAndUpdatesMission() throws Exception {
        missionHandler.sendStartCommand();

        verify(mqttClient, times(1)).publish(
                eq("api/requests/mission/123/vehicles/car-001"),
                argThat(msg -> {
                    String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                    return payload.contains("\"missionId\":\"123\"") &&
                            payload.contains("\"vin\":\"car-001\"");
                })
        );

        verify(mqttClient, times(1)).publish(
                eq("api/requests/mission/123/vehicles/car-002"),
                argThat(msg -> {
                    String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                    return payload.contains("\"missionId\":\"123\"") &&
                            payload.contains("\"vin\":\"car-002\"");
                })
        );

        verify(missionRepository, times(1)).save(mission);
        assertTrue(mission.isActive());
    }




    @Test
    void testHandleVehicleResponseValidPayloadUpdatesMissionStatus() throws Exception {


        Location location = new Location();
        location.setLat(40.0);
        location.setLng(-8.0);


        MQTTVehicleMissionStatus status = new MQTTVehicleMissionStatus();
        status.setVin("car-001");
        status.setTimestamp(System.currentTimeMillis());
        status.setMissionId(123L);
        status.setSpeed(90);
        status.setStatus(MissionStatus.IN_PROGRESS);
        status.setLocation(location);

        mission.setActive(true);
        String jsonPayload = objectMapper.writeValueAsString(status);
        MqttMessage mqttMessage = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));

        Mockito.when(missionRepository.findById(123L)).thenReturn(Optional.of(mission));

        boolean result = missionHandler.handleVehicleResponse("car-001", mqttMessage);

        assertTrue(result);  // mission is still active
        assertTrue(mission.isActive());  // mission is still active
        verify(missionRepository, times(1)).save(mission);
        assertEquals(MissionStatus.IN_PROGRESS, mission.getVehicleMissionDataList().get(0).getStatus());
    }


    @Test
    void testHandleVehicleResponse_invalidVIN_failsMission() throws Exception {
        String invalidJson = """
        {
            "vin": "",
            "timestamp": 123456789,
            "missionId": 123,
            "status": "IN_PROGRESS",
            "speed": 100,
            "location": { "latitude": 40.0, "longitude": -8.0 }
        }
        """;
        MqttMessage mqttMessage = new MqttMessage(invalidJson.getBytes(StandardCharsets.UTF_8));
        Mockito.when(missionRepository.findById(123L)).thenReturn(Optional.of(mission));

        boolean result = missionHandler.handleVehicleResponse("car-001", mqttMessage);

        assertFalse(result); // Mission failed due to error
        verify(missionRepository, atLeastOnce()).save(mission);
        assertFalse(mission.isActive());
        assertEquals(MissionStatus.FAILED, mission.getStatus());
    }


    @Test
    void testHandleVehicleResponseInvalidJsonFails() {
        String invalidJson = "{ malformed json";
        MqttMessage mqttMessage = new MqttMessage(invalidJson.getBytes(StandardCharsets.UTF_8));

        boolean result = missionHandler.handleVehicleResponse("car_001", mqttMessage);

        assertFalse(result); // Mission failed
        assertEquals(MissionStatus.FAILED, mission.getStatus());
        assertFalse(mission.isActive());
    }


    @Test
    void testHandleVehicleResponseOfUnassignedVehicle() throws Exception {


        Location location = new Location();
        location.setLat(40.0);
        location.setLng(-8.0);

        MQTTVehicleMissionStatus status = new MQTTVehicleMissionStatus();
        status.setVin("car-003"); // Not assigned to the mission
        status.setTimestamp(System.currentTimeMillis());
        status.setMissionId(123L);
        status.setSpeed(90);
        status.setStatus(MissionStatus.IN_PROGRESS);
        status.setLocation(location);

        String jsonPayload = objectMapper.writeValueAsString(status);
        MqttMessage mqttMessage = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));

        when(missionRepository.findById(123L)).thenReturn(Optional.of(mission));

        boolean result = missionHandler.handleVehicleResponse("car-003", mqttMessage);

        assertFalse(result);
        assertFalse(mission.isActive());
        assertEquals(MissionStatus.FAILED, mission.getStatus());
    }


    @Test
    void tesHandleCarSendingMessageAsAnotherCarWrongTopic() throws Exception {


        Location location = new Location();
        location.setLat(40.0);
        location.setLng(-8.0);

        MQTTVehicleMissionStatus status = new MQTTVehicleMissionStatus();
        status.setVin("car-001"); // Assigned to the mission
        status.setTimestamp(System.currentTimeMillis());
        status.setMissionId(123L);
        status.setSpeed(90);
        status.setStatus(MissionStatus.IN_PROGRESS);
        status.setLocation(location);

        String jsonPayload = objectMapper.writeValueAsString(status);
        MqttMessage mqttMessage = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));

        when(missionRepository.findById(123L)).thenReturn(Optional.of(mission));

        boolean result = missionHandler.handleVehicleResponse("car-003", mqttMessage); //Car not assigned

        assertFalse(result);
        assertFalse(mission.isActive());
        assertEquals(MissionStatus.FAILED, mission.getStatus());
    }



    @Test
    void tesHandleCarSendingMessageAsAnotherCarCorrectTopic() throws Exception {


        Location location = new Location();
        location.setLat(40.0);
        location.setLng(-8.0);

        MQTTVehicleMissionStatus status = new MQTTVehicleMissionStatus();
        status.setVin("car-002"); // Assigned to the mission but different from topic
        status.setTimestamp(System.currentTimeMillis());
        status.setMissionId(123L);
        status.setSpeed(90);
        status.setStatus(MissionStatus.IN_PROGRESS);
        status.setLocation(location);

        String jsonPayload = objectMapper.writeValueAsString(status);
        MqttMessage mqttMessage = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));

        when(missionRepository.findById(123L)).thenReturn(Optional.of(mission));

        boolean result = missionHandler.handleVehicleResponse("car-001", mqttMessage); // Assigned to the mission

        assertFalse(result);
        assertFalse(mission.isActive());
        assertEquals(MissionStatus.FAILED, mission.getStatus());
    }



}