package com.examble.drone_api.repository;

import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DroneRepositoryImpl implements DroneRepository {
    private final List<Drone> droneList = new ArrayList<>();
    private Long idCounter = 1L;

    @Override
    public List<Drone> findAll() {
        return droneList;
    }

    @Override
    public Optional<Drone> findById(Long id) {
        return droneList.stream()
                .filter(drone->drone.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Drone> findByDroneState(DroneState droneState) {
        return List.of();
    }

    @Override
    public Drone save(Drone drone) {
        drone.setId(idCounter++);
        droneList.add(drone);
        return drone;
    }
}
