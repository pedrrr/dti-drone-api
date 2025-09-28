package com.examble.drone_api.controller;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.dto.DroneResponseDTO;
import com.examble.drone_api.exception.ResourceNotFoundException;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.service.interfaces.DroneService;
import com.examble.drone_api.service.interfaces.DroneSimulationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DroneControllerTest {

    @Mock
    private DroneService droneService;

    @Mock
    private DroneSimulationService droneSimulationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DroneController(droneService, droneSimulationService))
                .setControllerAdvice(new com.examble.drone_api.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateDrone_ShouldReturnCreatedDrone() throws Exception {
        // Given
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(50, 20, 1, 1);

        Drone createdDrone = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .build();

        when(droneService.createDrone(any(DroneCreateRequestDTO.class))).thenReturn(createdDrone);

        mockMvc.perform(post("/api/v1/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.positionX").value(1))
                .andExpect(jsonPath("$.positionY").value(1))
                .andExpect(jsonPath("$.weightLimit").value(50))
                .andExpect(jsonPath("$.distancePerCargo").value(20))
                .andExpect(jsonPath("$.battery").value(100.0))
                .andExpect(jsonPath("$.state").value("IDLE"));

        verify(droneService).createDrone(any(DroneCreateRequestDTO.class));
    }

    @Test
    void testGetDrone_ShouldReturnDrone_WhenExists() throws Exception {
        Drone drone = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .build();

        when(droneService.findById(1L)).thenReturn(Optional.of(drone));

        mockMvc.perform(get("/api/v1/drones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.positionX").value(1))
                .andExpect(jsonPath("$.positionY").value(1))
                .andExpect(jsonPath("$.weightLimit").value(50))
                .andExpect(jsonPath("$.distancePerCargo").value(20))
                .andExpect(jsonPath("$.battery").value(100.0))
                .andExpect(jsonPath("$.state").value("IDLE"));

        verify(droneService).findById(1L);
    }

    @Test
    void testGetDrone_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(droneService.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/drones/1"))
                .andExpect(status().isNotFound());

        verify(droneService).findById(1L);
    }

    @Test
    void testGetAllDrones_ShouldReturnAllDrones() throws Exception {
        Drone drone1 = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .build();

        Drone drone2 = Drone.builder()
                .id(2L)
                .positionX(2)
                .positionY(2)
                .weightLimit(50)
                .distancePerCargo(15)
                .battery(80.0)
                .state(DroneState.IN_FLIGHT)
                .build();

        List<Drone> drones = Arrays.asList(drone1, drone2);
        when(droneService.findAll()).thenReturn(drones);

        mockMvc.perform(get("/api/v1/drones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(droneService).findAll();
    }

    @Test
    void testFlyDrones_ShouldReturnSuccess_WhenDronesHaveOrders() throws Exception {
        Drone drone1 = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .build();

        Drone drone2 = Drone.builder()
                .id(2L)
                .positionX(2)
                .positionY(2)
                .weightLimit(50)
                .distancePerCargo(15)
                .battery(80.0)
                .state(DroneState.IDLE)
                .build();

        List<Drone> drones = Arrays.asList(drone1, drone2);
        when(droneService.findAll()).thenReturn(drones);
        doNothing().when(droneSimulationService).startAllDronesWithOrders();

        mockMvc.perform(post("/api/v1/drones/fly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comando de voo executado com sucesso"))
                .andExpect(jsonPath("$.dronesWithOrders").value(0))
                .andExpect(jsonPath("$.dronesStarted").value(0))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(droneService).findAll();
        verify(droneSimulationService).startAllDronesWithOrders();
    }

    @Test
    void testFlyDrones_ShouldReturnError_WhenExceptionOccurs() throws Exception {
        when(droneService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/drones/fly"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erro ao iniciar voo dos drones: Database error"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(droneService).findAll();
        verify(droneSimulationService, never()).startAllDronesWithOrders();
    }

    @Test
    void testCreateDrone_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(0, 0, 0, 0); // Invalid values

        mockMvc.perform(post("/api/v1/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(droneService, never()).createDrone(any());
    }
}
