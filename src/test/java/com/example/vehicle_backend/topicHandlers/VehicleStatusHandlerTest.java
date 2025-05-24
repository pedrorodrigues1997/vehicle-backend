package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTStatusData;
import com.example.vehicle_backend.enums.HealthStatus;
import com.example.vehicle_backend.services.VehicleStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleStatusHandlerTest {


    private VehicleStatusService statusService;
    private VehicleStatusHandler handler;


    public static final String VEHICLE_STATUS_PAYLOAD = """
        {
          "vehicleId": "car-001",
          "timestamp": 1727171717171,
          "engineStatus": "OK",
          "engineOilLevelPercent": 97.54,
          "engineCheckEngineLight": false,
          "batteryStatus": "OK",
          "batteryVoltage": 3.74,
          "tireFrontLeftPsi": 2.21,
          "tireFrontRightPsi": 2.22,
          "tireRearLeftPsi": 2.22,
          "tireRearRightPsi": 2.01,
          "brakeStatus": "OK"
        }
        """;

    @BeforeEach
    void setUp() {
        statusService = Mockito.mock(VehicleStatusService.class);
        handler = new VehicleStatusHandler(statusService);
    }

    @Test
    void testHandle_validMessage_callsProcessVehicleStatus() {

        String vin = "car-001";
        String topic = "vehicles/" + vin + "/status";


        MqttMessage mqttMessage = new MqttMessage(VEHICLE_STATUS_PAYLOAD.getBytes(StandardCharsets.UTF_8));
        handler.handle(topic, mqttMessage);


        ArgumentCaptor<MQTTStatusData> captor = ArgumentCaptor.forClass(MQTTStatusData.class);
        verify(statusService, times(1)).processVehicleStatus(captor.capture());
        assertEquals(vin, captor.getValue().getVehicleId());
    }

    @Test
    void testHandleInvalidVinDoesNotCallService() {
        String invalidTopic = "vehicles/topic/status";
        MqttMessage mqttMessage = new MqttMessage("{}".getBytes(StandardCharsets.UTF_8));

        handler.handle(invalidTopic, mqttMessage);

        verifyNoInteractions(statusService);
    }

    @Test
    void testHandleEmptyPayloadDoesNotCallService() {
        String topic = "vehicles/car-001/status";
        MqttMessage emptyPayload = new MqttMessage("".getBytes(StandardCharsets.UTF_8));

        handler.handle(topic, emptyPayload);

        verifyNoInteractions(statusService);
    }


    @Test
    void testHandleMalformedJson() {
        String topic = "vehicles/car-001/status";
        String badJson = "{ vehicleId:\"car-001\" ";
        MqttMessage mqttMessage = new MqttMessage(badJson.getBytes(StandardCharsets.UTF_8));

        handler.handle(topic, mqttMessage);


        verifyNoInteractions(statusService);
    }

}