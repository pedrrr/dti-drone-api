package com.examble.drone_api.model;

import com.examble.drone_api.exception.DroneValidationException;
import com.examble.drone_api.model.type.DroneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {

    private Drone drone;
    private Order order;

    @BeforeEach
    void setUp() {
        drone = Drone.builder()
                .id(1L)
                .positionX(1)
                .positionY(1)
                .weightLimit(50)
                .distancePerCargo(20)
                .battery(100.0)
                .state(DroneState.IDLE)
                .orderList(new ArrayList<>())
                .build();

        order = Order.builder()
                .id(1L)
                .destinationX(5)
                .destinationY(5)
                .weight(10)
                .build();
    }

    @Test
    void testCanCarry_ShouldReturnTrue_WhenWeightWithinLimit() {
        // Given
        drone.getOrderList().add(Order.builder().weight(20).build());

        // When & Then
        assertTrue(drone.canCarry(order));
    }

    @Test
    void testCanCarry_ShouldReturnFalse_WhenWeightExceedsLimit() {
        // Given
        drone.getOrderList().add(Order.builder().weight(45).build());

        // When & Then
        assertFalse(drone.canCarry(order));
    }

    @Test
    void testCalculateDistance_ShouldReturnCorrectDistance() {
        // Given
        Order orderAtDistance = Order.builder()
                .destinationX(4)
                .destinationY(3)
                .build();

        // When
        double distance = drone.calculateDistance(orderAtDistance);

        // Then
        // Drone is at (1,1), order is at (4,3)
        // Distance = sqrt((4-1)² + (3-1)²) = sqrt(9 + 4) = sqrt(13) ≈ 3.606
        assertEquals(3.606, distance, 0.01);
    }

    @Test
    void testCanReach_ShouldReturnTrue_WhenWithinRange() {
        // Given
        Order nearbyOrder = Order.builder()
                .destinationX(10)
                .destinationY(10)
                .build();

        // When & Then
        assertTrue(drone.canReach(nearbyOrder));
    }

    @Test
    void testCanReach_ShouldReturnFalse_WhenOutOfRange() {
        // Given
        Order farOrder = Order.builder()
                .destinationX(100)
                .destinationY(100)
                .build();

        // When & Then
        assertFalse(drone.canReach(farOrder));
    }

    @Test
    void testAssignOrder_ShouldAddOrder_WhenValid() {
        // When
        drone.assignOrder(order);

        // Then
        assertEquals(1, drone.getOrderList().size());
        assertEquals(order, drone.getOrderList().get(0));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenWeightExceedsLimit() {
        // Given
        drone.getOrderList().add(Order.builder().weight(45).build());

        // When & Then
        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(order)
        );
        assertTrue(exception.getMessage().contains("Peso atual"));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenOutOfRange() {
        // Given
        Order farOrder = Order.builder()
                .destinationX(100)
                .destinationY(100)
                .weight(5)
                .build();

        // When & Then
        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(farOrder)
        );
        assertTrue(exception.getMessage().contains("Distância"));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenNotAvailable() {
        // Given
        drone.setState(DroneState.IN_FLIGHT);

        // When & Then
        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(order)
        );
        assertTrue(exception.getMessage().contains("não disponível"));
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnTrue_WhenIdleAndBatteryGood() {
        // When & Then
        assertTrue(drone.isAvailableForOrders());
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnFalse_WhenNotIdle() {
        // Given
        drone.setState(DroneState.IN_FLIGHT);

        // When & Then
        assertFalse(drone.isAvailableForOrders());
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnFalse_WhenLowBattery() {
        // Given
        drone.setBattery(15.0);

        // When & Then
        assertFalse(drone.isAvailableForOrders());
    }

    @Test
    void testNeedsRecharging_ShouldReturnTrue_WhenAtBaseWithLowBattery() {
        // Given
        drone.setBattery(70.0);

        // When & Then
        assertTrue(drone.needsRecharging());
    }

    @Test
    void testNeedsRecharging_ShouldReturnFalse_WhenNotAtBase() {
        // Given
        drone.setPositionX(5);
        drone.setPositionY(5);
        drone.setBattery(70.0);

        // When & Then
        assertFalse(drone.needsRecharging());
    }

    @Test
    void testNeedsRecharging_ShouldReturnFalse_WhenBatteryGood() {
        // Given
        drone.setBattery(85.0);

        // When & Then
        assertFalse(drone.needsRecharging());
    }

    @Test
    void testIsAtBase_ShouldReturnTrue_WhenAtBasePosition() {
        // When & Then
        assertTrue(drone.isAtBase());
    }

    @Test
    void testIsAtBase_ShouldReturnFalse_WhenNotAtBase() {
        // Given
        drone.setPositionX(5);
        drone.setPositionY(5);

        // When & Then
        assertFalse(drone.isAtBase());
    }

    @Test
    void testCalculateBatteryConsumption_ShouldReturnCorrectValue() {
        // When
        double consumption = drone.calculateBatteryConsumption(10.0);

        // Then
        assertEquals(1.0, consumption, 0.01); // 10 * 0.1
    }

    @Test
    void testUpdatePosition_ShouldUpdatePositionAndConsumeBattery() {
        // Given
        double initialBattery = drone.getBattery();

        // When
        drone.updatePosition(5, 5);

        // Then
        assertEquals(5, drone.getPositionX());
        assertEquals(5, drone.getPositionY());
        assertTrue(drone.getBattery() < initialBattery);
    }

    @Test
    void testUpdatePosition_ShouldNotGoBelowZeroBattery() {
        // Given
        drone.setBattery(0.1);

        // When
        drone.updatePosition(100, 100);

        // Then
        assertEquals(0.0, drone.getBattery(), 0.01);
    }

    @Test
    void testChangeState_ShouldUpdateStateAndTimestamp() {
        // Given
        LocalDateTime beforeChange = LocalDateTime.now().minusSeconds(1);

        // When
        drone.changeState(DroneState.IN_FLIGHT);

        // Then
        assertEquals(DroneState.IN_FLIGHT, drone.getState());
        assertNotNull(drone.getLastStateChange());
        assertTrue(drone.getLastStateChange().isAfter(beforeChange));
    }

    @Test
    void testGetTotalWeight_ShouldReturnCorrectWeight() {
        // Given
        drone.getOrderList().add(Order.builder().weight(10).build());
        drone.getOrderList().add(Order.builder().weight(15).build());

        // When
        int totalWeight = drone.getTotalWeight();

        // Then
        assertEquals(25, totalWeight);
    }

    @Test
    void testGetTotalWeight_ShouldReturnZero_WhenNoOrders() {
        // When
        int totalWeight = drone.getTotalWeight();

        // Then
        assertEquals(0, totalWeight);
    }

    @Test
    void testHasOrders_ShouldReturnTrue_WhenOrdersExist() {
        // Given
        drone.getOrderList().add(order);

        // When & Then
        assertTrue(drone.hasOrders());
    }

    @Test
    void testHasOrders_ShouldReturnFalse_WhenNoOrders() {
        // When & Then
        assertFalse(drone.hasOrders());
    }
}
