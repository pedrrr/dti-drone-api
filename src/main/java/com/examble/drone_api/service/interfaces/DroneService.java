package com.examble.drone_api.service.interfaces;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.model.Drone;

import java.util.List;
import java.util.Optional;

public interface DroneService {
    List<Drone> findAll();
    Optional<Drone> findById(Long id);
    Drone createDrone(DroneCreateRequestDTO droneCreateRequestDTO);
}
