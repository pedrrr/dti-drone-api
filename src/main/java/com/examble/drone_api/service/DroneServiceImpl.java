package com.examble.drone_api.service;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.mapper.DroneMapper;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import com.examble.drone_api.service.interfaces.DroneService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneServiceImpl implements DroneService {

    public DroneRepository droneRepository;
    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public List<Drone> findAll(){
        return droneRepository.findAll();
    }

    @Override
    public Optional<Drone> findById(Long id){
        return droneRepository.findById(id);
    }

    @Override
    public Drone createDrone(DroneCreateRequestDTO droneCreateRequestDTO){
        Drone drone = DroneMapper.INSTANCE.toEntity(droneCreateRequestDTO);
        
        // Inicializar campos padrão
        drone.setBattery(100.0);
        drone.setState(com.examble.drone_api.model.type.DroneState.IDLE);
        drone.setLastStateChange(java.time.LocalDateTime.now());
        drone.setOrderList(new java.util.ArrayList<>());
        
        return droneRepository.save(drone);
    }
}
