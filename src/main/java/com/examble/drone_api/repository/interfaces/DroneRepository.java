package com.examble.drone_api.repository.interfaces;

import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.type.DroneState;

import java.util.List;
import java.util.Optional;

public interface DroneRepository {
    List<Drone> findAll();
    Optional<Drone> findById(Long id);
    List<Drone> findByDroneState(DroneState droneState);
    Drone save(Drone drone);
}
