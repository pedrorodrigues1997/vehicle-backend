package com.example.vehicle_backend.validators;

import com.example.vehicle_backend.dto.MqttResponses.MQTTVehicleMissionStatus;
import com.example.vehicle_backend.entities.Location;
import com.example.vehicle_backend.entities.Mission;
import com.example.vehicle_backend.entities.VehicleMissionData;
import com.example.vehicle_backend.enums.MissionStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.regex.Pattern;

public class CommonDataValidator {
    private static final Pattern VIN_PATTERN = Pattern.compile("^car-\\d{3}$");
    private static final long ALLOWED_TIME_DIFF = 60000; //milliseconds
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();



    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation errors: ");
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("; ");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }




    public static void validateTimestamp(long timestamp) {
        if (timestamp == 0) {
            throw new IllegalArgumentException("Missing timestamp.");
        }
        validateTimestampInterval(timestamp);
    }


    private static void validateTimestampInterval(long timestamp) {
        long now = System.currentTimeMillis();

        if (Math.abs(timestamp - now) > ALLOWED_TIME_DIFF) {
            throw new IllegalArgumentException("Timestamp too far in the future or past: " + timestamp);
        }
    }


    public static void validateStatusProgression(MissionStatus currentMissionStatus, MissionStatus missionStatusToValidate) {


        if (missionStatusToValidate != null) {

            switch (currentMissionStatus) {
                case FAILED:
                case COMPLETED:
                case CANCELLED:
                    if (missionStatusToValidate != currentMissionStatus) {
                        throw new IllegalArgumentException("Cannot change mission status from a final state");
                    }
                    break;
                case PENDING:
                    if (missionStatusToValidate != MissionStatus.IN_PROGRESS
                            && missionStatusToValidate != MissionStatus.CANCELLED
                            && missionStatusToValidate != MissionStatus.PENDING) {
                        throw new IllegalArgumentException("Illegal Status Transition to " + missionStatusToValidate);
                    }
                    break;
                case IN_PROGRESS:
                    if (missionStatusToValidate == MissionStatus.PENDING) {
                        throw new IllegalArgumentException("Illegal Status Transition to PENDING");
                    }

            }


        }
    }

    public static void validateUsername(String username) {
        if (isBlank(username)) {
            throw new IllegalArgumentException("Invalid or missing username.");
        }

        if (username.length() < 4) {
            throw new IllegalArgumentException("Username is too short! Minimum 4 characters.");
        }
    }

    public static void validatePassword(String username) {
        if (isBlank(username)) {
            throw new IllegalArgumentException("Invalid or missing password.");
        }

        if (username.length() < 8) {
            throw new IllegalArgumentException("Password is too short! Minimum 8 characters.");
        }
    }


    public static void validateVIN(String vinToValidate) {

        //Assuming that for this project VIN is always similar to car-00n
        if (isBlank(vinToValidate) || !VIN_PATTERN.matcher(vinToValidate).matches()) {
            throw new IllegalArgumentException("Invalid or missing VIN. Expected pattern: car-XXX");
        }
    }




    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}