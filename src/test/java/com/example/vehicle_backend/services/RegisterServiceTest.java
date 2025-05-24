package com.example.vehicle_backend.services;

import com.example.vehicle_backend.entities.Vehicle;
import com.example.vehicle_backend.repositories.VehicleRepository;
import com.example.vehicle_backend.validators.CommonDataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class RegisterServiceTest {


    private VehicleRepository vehicleRepository;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;
    private RegisterService registerService;

    public static final String VALID_PAYLOAD = """
        {
            "vin": "car-004",
            "model": "Model S",
            "manufacturer": "Tesla",
            "firmwareVersion": "v10.2.1",
            "hardwareId": "HW-12345",
            "secretToken": "password123",
            "timestamp": 1710000000
        }
        """;


    public static final String INVALID_VIN_PAYLOAD = """
        {
            "vin": "car214141-004",
            "model": "Model S",
            "manufacturer": "Tesla",
            "firmwareVersion": "v10.2.1",
            "hardwareId": "HW-12345",
            "secretToken": "password123",
            "timestamp": 1710000000
        }
        """;

    @BeforeEach
    void setUp() {
        vehicleRepository = Mockito.mock(VehicleRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        objectMapper = new ObjectMapper();
        registerService = new RegisterService(vehicleRepository, passwordEncoder, objectMapper);
    }


    @Test
    void testProcessRegisterSuccessfulRegistration() {
        Mockito.when(vehicleRepository.findByVin("car-004")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        try (var mock = Mockito.mockStatic(CommonDataValidator.class)) {
            // Skip throwing anything from validator
            mock.when(() -> CommonDataValidator.validate(any())).thenCallRealMethod();


            assertDoesNotThrow(() -> registerService.processRegister(VALID_PAYLOAD));
            Mockito.verify(vehicleRepository).save(any(Vehicle.class));
        }
    }


    @Test
    void testProcessRegisterInvalidJson() {
        String badJson = "{ bad json";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            registerService.processRegister(badJson);
        });

        assertTrue(ex.getMessage().contains("Malformed JSON"));
    }

    @Test
    void testProcessRegister_ExistingVehicleThrows() {
        Mockito.when(vehicleRepository.findByVin("car-004")).thenReturn(Optional.of(new Vehicle()));

        try (var mock = Mockito.mockStatic(CommonDataValidator.class)) {
            mock.when(() -> CommonDataValidator.validate(any())).thenCallRealMethod();

            Exception ex = assertThrows(IllegalArgumentException.class, () -> {
                registerService.processRegister(VALID_PAYLOAD);
            });

            assertEquals("Vehicle already registered with this VIN.", ex.getMessage());
        }
    }


    @Test
    void testProcessRegisterInvalidVINThrows() {
        try (var mock = Mockito.mockStatic(CommonDataValidator.class)) {
            mock.when(() -> CommonDataValidator.validate(any())).thenCallRealMethod();
            mock.when(() -> CommonDataValidator.validateVIN(any())).thenCallRealMethod();

            Exception ex = assertThrows(IllegalArgumentException.class, () -> {
                registerService.processRegister(INVALID_VIN_PAYLOAD);
            });

            assertEquals("Invalid or missing VIN. Expected pattern: car-XXX", ex.getMessage());
        }
    }


}