package com.example.vehicle_backend.repositories;

import com.example.vehicle_backend.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

        Optional<Vehicle> findByVin(String vin);
        boolean existsByVin(String vin);
        //long countByVin(List<String> vins);

}
