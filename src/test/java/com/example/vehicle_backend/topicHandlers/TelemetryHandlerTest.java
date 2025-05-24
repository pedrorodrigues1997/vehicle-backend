package com.example.vehicle_backend.topicHandlers;

import com.example.vehicle_backend.dto.MqttResponses.MQTTStatusData;
import com.example.vehicle_backend.dto.MqttResponses.MQTTTelemetryData;
import com.example.vehicle_backend.services.TelemetryService;
import com.example.vehicle_backend.services.VehicleStatusService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

class TelemetryHandlerTest {


    private TelemetryService telemetryService;
    private TelemetryHandler handler;

    public static final String VEHICLE_TELEMETRY_PAYLOAD = """
        {
             "vin": "car-001",
             "timestamp": 1727171717171,
             "lat": 41.45,
             "lng": -37.98,
             "speed": 110

        }
        """;

    @BeforeEach
    void setUp() {
        telemetryService = Mockito.mock(TelemetryService.class);
        handler = new TelemetryHandler(telemetryService);
    }


    @Test
    void testHandleValidMessageCallsProcessVehicleTelemetry() {

        String vin = "car-001";
        String topic = "vehicles/" + vin + "/telemetry";

        MqttMessage mqttMessage = new MqttMessage(VEHICLE_TELEMETRY_PAYLOAD.getBytes(StandardCharsets.UTF_8));
        handler.handle(topic, mqttMessage);


        ArgumentCaptor<MQTTTelemetryData> captor = ArgumentCaptor.forClass(MQTTTelemetryData.class);
        verify(telemetryService, times(1)).processTelemetry(captor.capture());
        assertEquals(vin, captor.getValue().getVin());
    }

    @Test
    void testHandleInvalidVinDoesNotCallService() {
        String invalidTopic = "vehicles/car-002/telemetry";
        MqttMessage mqttMessage = new MqttMessage("{}".getBytes(StandardCharsets.UTF_8));

        handler.handle(invalidTopic, mqttMessage);

        verifyNoInteractions(telemetryService);
    }

    @Test
    void testHandleEmptyPayloadDoesNotCallService() {
        String topic = "vehicles/car-001/status";
        MqttMessage emptyPayload = new MqttMessage("".getBytes(StandardCharsets.UTF_8));

        handler.handle(topic, emptyPayload);

        verifyNoInteractions(telemetryService);
    }


    @Test
    void testHandleMalformedJson() {
        String topic = "vehicles/car-001/telemetry";
        String badJson = "{ vin:\"car-001\" ";
        MqttMessage mqttMessage = new MqttMessage(badJson.getBytes(StandardCharsets.UTF_8));

        handler.handle(topic, mqttMessage);


        verifyNoInteractions(telemetryService);
    }

}