package com.examble.drone_api.controller;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.dto.DroneResponseDTO;
import com.examble.drone_api.exception.ResourceNotFoundException;
import com.examble.drone_api.mapper.DroneMapper;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.service.interfaces.DashboardService;
import com.examble.drone_api.service.interfaces.DroneService;
import com.examble.drone_api.service.interfaces.DroneSimulationService;
import com.examble.drone_api.service.DashboardServiceImpl;
import com.examble.drone_api.dto.DashboardResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/drones")
@Slf4j
public class DroneController {

    DroneService droneService;
    DroneSimulationService droneSimulationService;
    DashboardService dashboardService;

    public DroneController(DroneService droneService, DroneSimulationService droneSimulationService, DashboardService dashboardService) {
        this.droneService = droneService;
        this.droneSimulationService = droneSimulationService;
        this.dashboardService = dashboardService;
    }

    @PostMapping
    public ResponseEntity<DroneResponseDTO> postDrone(@RequestBody @Valid
                                                      DroneCreateRequestDTO droneCreateRequestDTO) {
        Drone newDrone = droneService.createDrone(droneCreateRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDrone.getId())
                .toUri();
        return ResponseEntity.created(uri).body(DroneMapper.INSTANCE.toDTO(newDrone));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> getDrone(@PathVariable Long id) {
        Drone drone = droneService.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.droneNotFound(id));
        
        return ResponseEntity.ok(DroneMapper.INSTANCE.toDTO(drone));
    }

    @GetMapping
    public ResponseEntity<List<DroneResponseDTO>> getAllDrones() {
        return ResponseEntity.ok(droneService.findAll().stream()
                .map(DroneMapper.INSTANCE::toDTO)
                .toList());
    }

    @PostMapping("/fly")
    public ResponseEntity<Map<String, Object>> flyDrones(){
        try {
            List<Drone> drones = droneService.findAll();
            int dronesStarted = 0;
            int dronesWithOrders = 0;
            
            // Contar drones com pedidos
            for (Drone drone : drones) {
                if (drone.hasOrders()) {
                    dronesWithOrders++;
                }
            }
            
            // Iniciar voo de todos os drones com pedidos
            droneSimulationService.startAllDronesWithOrders();
            
            // Contar quantos drones efetivamente iniciaram voo
            for (Drone drone : drones) {
                if (drone.getState() == com.examble.drone_api.model.type.DroneState.IN_FLIGHT) {
                    dronesStarted++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comando de voo executado com sucesso");
            response.put("dronesWithOrders", dronesWithOrders);
            response.put("dronesStarted", dronesStarted);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao iniciar voo dos drones: " + e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        try {
            DashboardResponseDTO dashboardData = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Erro ao obter dados do dashboard", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
