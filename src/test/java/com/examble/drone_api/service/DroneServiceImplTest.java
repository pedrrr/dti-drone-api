package com.examble.drone_api.service;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    private DroneServiceImpl droneService;

    @BeforeEach
    void setUp() {
        droneService = new DroneServiceImpl(droneRepository);
    }

    @Test
    void testFindAll_ShouldReturnAllDrones() {
        // Given
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

        List<Drone> expectedDrones = Arrays.asList(drone1, drone2);
        when(droneRepository.findAll()).thenReturn(expectedDrones);

        // When
        List<Drone> result = droneService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedDrones, result);
        verify(droneRepository).findAll();
    }

    @Test
    void testFindAll_ShouldReturnEmptyList_WhenNoDrones() {
        // Given
        when(droneRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Drone> result = droneService.findAll();

        // Then
        assertTrue(result.isEmpty());
        verify(droneRepository).findAll();
    }

    @Test
    void testFindById_ShouldReturnDrone_WhenExists() {
        // Given
        Drone expectedDrone = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .build();

        when(droneRepository.findById(1L)).thenReturn(Optional.of(expectedDrone));

        // When
        Optional<Drone> result = droneService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedDrone, result.get());
        verify(droneRepository).findById(1L);
    }

    @Test
    void testFindById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(droneRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Drone> result = droneService.findById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(droneRepository).findById(1L);
    }

    @Test
    void testCreateDrone_ShouldCreateDroneWithDefaultValues() {
        // Given
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(40, 25, 5, 10);

        Drone savedDrone = Drone.builder()
                .id(1L)
                .positionX(5)
                .positionY(10)
                .weightLimit(50)
                .distancePerCargo(25)
                .battery(100.0)
                .state(DroneState.IDLE)
                .lastStateChange(LocalDateTime.now())
                .orderList(new java.util.ArrayList<>())
                .build();

        when(droneRepository.save(any(Drone.class))).thenReturn(savedDrone);

        // When
        Drone result = droneService.createDrone(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getPositionX());
        assertEquals(10, result.getPositionY());
        assertEquals(50, result.getWeightLimit());
        assertEquals(25, result.getDistancePerCargo());
        assertEquals(100.0, result.getBattery());
        assertEquals(DroneState.IDLE, result.getState());
        assertNotNull(result.getLastStateChange());
        assertNotNull(result.getOrderList());
        assertTrue(result.getOrderList().isEmpty());

        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    void testCreateDrone_ShouldSetDefaultValues_WhenNotProvided() {
        // Given
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(0, 0, 0, 0);

        Drone savedDrone = Drone.builder()
                .id(1L)
                .positionX(0)
                .positionY(0)
                .weightLimit(50)
                .distancePerCargo(0)
                .battery(100.0)
                .state(DroneState.IDLE)
                .lastStateChange(LocalDateTime.now())
                .orderList(new java.util.ArrayList<>())
                .build();

        when(droneRepository.save(any(Drone.class))).thenReturn(savedDrone);

        // When
        Drone result = droneService.createDrone(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(100.0, result.getBattery());
        assertEquals(DroneState.IDLE, result.getState());
        assertNotNull(result.getLastStateChange());
        assertNotNull(result.getOrderList());
        assertTrue(result.getOrderList().isEmpty());

        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    void testCreateDrone_ShouldHandleRepositoryException() {
        // Given
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(50, 20, 1, 1);

        when(droneRepository.save(any(Drone.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> droneService.createDrone(requestDTO));
        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    void testCreateDrone_ShouldPreserveRequestData() {
        // Given
        DroneCreateRequestDTO requestDTO = new DroneCreateRequestDTO(75, 30, 15, 25);

        Drone savedDrone = Drone.builder()
                .id(1L)
                .positionX(15)
                .positionY(25)
                .weightLimit(50)
                .distancePerCargo(30)
                .battery(100.0)
                .state(DroneState.IDLE)
                .lastStateChange(LocalDateTime.now())
                .orderList(new java.util.ArrayList<>())
                .build();

        when(droneRepository.save(any(Drone.class))).thenReturn(savedDrone);

        // When
        Drone result = droneService.createDrone(requestDTO);

        // Then
        assertEquals(15, result.getPositionX());
        assertEquals(25, result.getPositionY());
        assertEquals(50, result.getWeightLimit());
        assertEquals(30, result.getDistancePerCargo());

        verify(droneRepository).save(any(Drone.class));
    }
}
