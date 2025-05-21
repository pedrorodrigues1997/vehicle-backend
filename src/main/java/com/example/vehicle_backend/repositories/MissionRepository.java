package com.example.vehicle_backend.repositories;

import com.example.vehicle_backend.entities.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    boolean existsByMissionName(String missionName);

    @Query(value = """
    SELECT mvd.vehicle_id
    FROM mission_vehicle_data mvd
    JOIN missions m ON m.mission_id = mvd.mission_id
    WHERE m.is_active = true
    AND mvd.vehicle_id IN (:vins)
    """, nativeQuery = true)
    List<String> findVehiclesInOngoingMissions(@Param("vins") List<String> vins);

}
