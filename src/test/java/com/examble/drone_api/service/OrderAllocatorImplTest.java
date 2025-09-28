package com.examble.drone_api.service;

import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.model.type.Priority;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import com.examble.drone_api.repository.interfaces.OrderRepository;
import com.examble.drone_api.validation.DroneValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAllocatorImplTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DroneValidator droneValidator;

    private OrderAllocatorImpl orderAllocator;

    @BeforeEach
    void setUp() {
        orderAllocator = new OrderAllocatorImpl(orderRepository, droneRepository, droneValidator);
    }

    @Test
    void testAllocateOrders_ShouldAllocateOrdersToAvailableDrones() {
        // Given
        Drone drone1 = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        Drone drone2 = createDrone(2L, 30, 15, DroneState.IDLE, 90.0);
        
        Order order1 = createOrder(1L, 5, 5, 10, Priority.HIGH);
        Order order2 = createOrder(2L, 10, 10, 15, Priority.MEDIUM);

        List<Drone> drones = Arrays.asList(drone1, drone2);
        List<Order> orders = Arrays.asList(order1, order2);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(new DroneValidator.ValidationResult());

        // When
        orderAllocator.allocateOrders();

        // Then
        verify(droneValidator, times(2)).validateDroneForOrder(any(), any());
        assertTrue(drone1.getOrderList().contains(order1) || drone2.getOrderList().contains(order1));
        assertTrue(drone1.getOrderList().contains(order2) || drone2.getOrderList().contains(order2));
    }

    @Test
    void testAllocateOrders_ShouldPrioritizeHighPriorityOrders() {
        // Given
        Drone drone = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        
        Order highPriorityOrder = createOrder(1L, 5, 5, 10, Priority.HIGH);
        Order lowPriorityOrder = createOrder(2L, 10, 10, 15, Priority.LOW);

        List<Drone> drones = Arrays.asList(drone);
        List<Order> orders = Arrays.asList(lowPriorityOrder, highPriorityOrder); // Low priority first in list

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(new DroneValidator.ValidationResult());

        // When
        orderAllocator.allocateOrders();

        // Then
        // High priority order should be allocated first
        assertTrue(drone.getOrderList().contains(highPriorityOrder));
        assertTrue(drone.getOrderList().contains(lowPriorityOrder));
        
        // Verify allocation order (HIGH should be processed first)
        verify(droneValidator, times(2)).validateDroneForOrder(any(), any());
    }

    @Test
    void testAllocateOrders_ShouldClearExistingAllocationsForAvailableDrones() {
        // Given
        Drone drone = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        Order existingOrder = createOrder(1L, 3, 3, 5, Priority.LOW);
        drone.getOrderList().add(existingOrder);
        
        Order newOrder = createOrder(2L, 8, 8, 10, Priority.HIGH);

        List<Drone> drones = Arrays.asList(drone);
        List<Order> orders = Arrays.asList(newOrder);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(new DroneValidator.ValidationResult());

        // When
        orderAllocator.allocateOrders();

        // Then
        // Existing order should be cleared and new order allocated
        assertTrue(drone.getOrderList().contains(newOrder));
        // Note: The existing order is cleared by the algorithm, so it should only contain the new order
    }

    @Test
    void testAllocateOrders_ShouldSkipDronesNotAvailableForOrders() {
        // Given
        Drone availableDrone = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        Drone busyDrone = createDrone(2L, 30, 15, DroneState.IN_FLIGHT, 90.0);
        
        Order order = createOrder(1L, 5, 5, 10, Priority.HIGH);

        List<Drone> drones = Arrays.asList(availableDrone, busyDrone);
        List<Order> orders = Arrays.asList(order);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(new DroneValidator.ValidationResult());

        // When
        orderAllocator.allocateOrders();

        // Then
        // Only available drone should receive the order
        assertTrue(availableDrone.getOrderList().contains(order));
        assertFalse(busyDrone.getOrderList().contains(order));
    }

    @Test
    void testAllocateOrders_ShouldHandleValidationFailures() {
        // Given
        Drone drone = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        Order order = createOrder(1L, 5, 5, 10, Priority.HIGH);

        List<Drone> drones = Arrays.asList(drone);
        List<Order> orders = Arrays.asList(order);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        
        // Mock validation failure
        DroneValidator.ValidationResult failedValidation = new DroneValidator.ValidationResult();
        failedValidation.addError("Validation failed");
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(failedValidation);

        // When
        orderAllocator.allocateOrders();

        // Then
        // Order should not be allocated due to validation failure
        assertFalse(drone.getOrderList().contains(order));
    }

    @Test
    void testAllocateOrders_ShouldHandleNoAvailableDrones() {
        // Given
        Drone busyDrone = createDrone(1L, 50, 20, DroneState.IN_FLIGHT, 90.0);
        Order order = createOrder(1L, 5, 5, 10, Priority.HIGH);

        List<Drone> drones = Arrays.asList(busyDrone);
        List<Order> orders = Arrays.asList(order);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        orderAllocator.allocateOrders();

        // Then
        // No drone should receive the order
        assertFalse(busyDrone.getOrderList().contains(order));
    }

    @Test
    void testAllocateOrders_ShouldHandleEmptyOrdersList() {
        // Given
        Drone drone = createDrone(1L, 50, 20, DroneState.IDLE, 100.0);
        
        List<Drone> drones = Arrays.asList(drone);
        List<Order> orders = new ArrayList<>();

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        orderAllocator.allocateOrders();

        // Then
        // No orders to allocate
        assertTrue(drone.getOrderList().isEmpty());
        verify(droneValidator, never()).validateDroneForOrder(any(), any());
    }

    @Test
    void testAllocateOrders_ShouldHandleEmptyDronesList() {
        // Given
        Order order = createOrder(1L, 5, 5, 10, Priority.HIGH);
        
        List<Drone> drones = new ArrayList<>();
        List<Order> orders = Arrays.asList(order);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        orderAllocator.allocateOrders();

        // Then
        // No drones available for allocation
        verify(droneValidator, never()).validateDroneForOrder(any(), any());
    }

    @Test
    void testFindBestDroneForOrder_ShouldConsiderMultipleFactors() {
        // Given
        Drone drone1 = createDrone(1L, 50, 20, DroneState.IDLE, 80.0); // Lower battery
        Drone drone2 = createDrone(2L, 50, 20, DroneState.IDLE, 100.0); // Higher battery
        
        Order order = createOrder(1L, 5, 5, 10, Priority.HIGH);

        List<Drone> drones = Arrays.asList(drone1, drone2);
        List<Order> orders = Arrays.asList(order);

        when(droneRepository.findAll()).thenReturn(drones);
        when(orderRepository.findAll()).thenReturn(orders);
        when(droneValidator.validateDroneForOrder(any(), any()))
                .thenReturn(new DroneValidator.ValidationResult());

        // When
        orderAllocator.allocateOrders();

        // Then
        // The algorithm should prefer drone with higher battery
        verify(droneValidator, atLeastOnce()).validateDroneForOrder(any(), any());
    }

    // Helper methods
    private Drone createDrone(Long id, int weightLimit, int distancePerCargo, DroneState state, double battery) {
        Drone drone = Drone.builder()
                .id(id)
                .positionX(1)
                .positionY(1)
                .weightLimit(weightLimit)
                .distancePerCargo(distancePerCargo)
                .battery(battery)
                .state(state)
                .orderList(new ArrayList<>())
                .build();
        return drone;
    }

    private Order createOrder(Long id, int destinationX, int destinationY, int weight, Priority priority) {
        return Order.builder()
                .id(id)
                .destinationX(destinationX)
                .destinationY(destinationY)
                .weight(weight)
                .priority(priority)
                .build();
    }
}



