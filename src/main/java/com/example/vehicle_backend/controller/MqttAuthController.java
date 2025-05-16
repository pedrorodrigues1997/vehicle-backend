package com.example.vehicle_backend.controller;


import com.example.vehicle_backend.dto.MqttAuthRequest;
import com.example.vehicle_backend.enums.EMQXResponses;
import com.example.vehicle_backend.model.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mqtt")
public class MqttAuthController {


    /**
     * Class is to be used by MQTT Broker to authenticate the vehicles.
     */

    private final VehicleRepository vehicleRepository;
    private PasswordEncoder passwordEncoder;


    public MqttAuthController(VehicleRepository vehicleRepository, PasswordEncoder passwordEncoder) {
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody MqttAuthRequest request){

        String vin = request.getUsername();
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVin(vin);

        if(vehicleOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createResponseForBroker(EMQXResponses.ignore));
        }

        Vehicle vehicle = vehicleOpt.get();

        boolean passwordMatcher = passwordEncoder.matches(request.getPassword(), vehicle.getMqttToken());
        if(!passwordMatcher){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createResponseForBroker(EMQXResponses.deny));
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createResponseForBroker(EMQXResponses.allow));

    }

    private static Map<String, EMQXResponses> createResponseForBroker(EMQXResponses status) {
        Map<String, EMQXResponses> response = new HashMap<>();
        response.put("result", status);
        return response;
    }

}
