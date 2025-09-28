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
        drone.getOrderList().add(Order.builder().weight(20).build());

        assertTrue(drone.canCarry(order));
    }

    @Test
    void testCanCarry_ShouldReturnFalse_WhenWeightExceedsLimit() {
        drone.getOrderList().add(Order.builder().weight(45).build());

        assertFalse(drone.canCarry(order));
    }

    @Test
    void testCalculateDistance_ShouldReturnCorrectDistance() {
        Order orderAtDistance = Order.builder()
                .destinationX(4)
                .destinationY(3)
                .build();

        double distance = drone.calculateDistance(orderAtDistance);

        assertEquals(3.606, distance, 0.01);
    }

    @Test
    void testCanReach_ShouldReturnTrue_WhenWithinRange() {
        Order nearbyOrder = Order.builder()
                .destinationX(10)
                .destinationY(10)
                .build();

        assertTrue(drone.canReach(nearbyOrder));
    }

    @Test
    void testCanReach_ShouldReturnFalse_WhenOutOfRange() {
        Order farOrder = Order.builder()
                .destinationX(100)
                .destinationY(100)
                .build();

        assertFalse(drone.canReach(farOrder));
    }

    @Test
    void testAssignOrder_ShouldAddOrder_WhenValid() {
        drone.assignOrder(order);

        assertEquals(1, drone.getOrderList().size());
        assertEquals(order, drone.getOrderList().get(0));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenWeightExceedsLimit() {
        drone.getOrderList().add(Order.builder().weight(45).build());

        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(order)
        );
        assertTrue(exception.getMessage().contains("Peso atual"));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenOutOfRange() {
        Order farOrder = Order.builder()
                .destinationX(100)
                .destinationY(100)
                .weight(5)
                .build();

        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(farOrder)
        );
        assertTrue(exception.getMessage().contains("Distância"));
    }

    @Test
    void testAssignOrder_ShouldThrowException_WhenNotAvailable() {
        drone.setState(DroneState.IN_FLIGHT);

        DroneValidationException exception = assertThrows(
                DroneValidationException.class,
                () -> drone.assignOrder(order)
        );
        assertTrue(exception.getMessage().contains("não disponível"));
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnTrue_WhenIdleAndBatteryGood() {
        assertTrue(drone.isAvailableForOrders());
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnFalse_WhenNotIdle() {
        drone.setState(DroneState.IN_FLIGHT);

        assertFalse(drone.isAvailableForOrders());
    }

    @Test
    void testIsAvailableForOrders_ShouldReturnFalse_WhenLowBattery() {
        drone.setBattery(15.0);

        assertFalse(drone.isAvailableForOrders());
    }

    @Test
    void testNeedsRecharging_ShouldReturnTrue_WhenAtBaseWithLowBattery() {
        drone.setBattery(70.0);

        assertTrue(drone.needsRecharging());
    }

    @Test
    void testNeedsRecharging_ShouldReturnFalse_WhenNotAtBase() {
        drone.setPositionX(5);
        drone.setPositionY(5);
        drone.setBattery(70.0);

        assertFalse(drone.needsRecharging());
    }

    @Test
    void testNeedsRecharging_ShouldReturnFalse_WhenBatteryGood() {
        drone.setBattery(85.0);

        assertFalse(drone.needsRecharging());
    }

    @Test
    void testIsAtBase_ShouldReturnTrue_WhenAtBasePosition() {
        assertTrue(drone.isAtBase());
    }

    @Test
    void testIsAtBase_ShouldReturnFalse_WhenNotAtBase() {
        drone.setPositionX(5);
        drone.setPositionY(5);

        assertFalse(drone.isAtBase());
    }

    @Test
    void testCalculateBatteryConsumption_ShouldReturnCorrectValue() {
        double consumption = drone.calculateBatteryConsumption(10.0);

        assertEquals(1.0, consumption, 0.01); // 10 * 0.1
    }

    @Test
    void testUpdatePosition_ShouldUpdatePositionAndConsumeBattery() {
        double initialBattery = drone.getBattery();

        drone.updatePosition(5, 5);

        assertEquals(5, drone.getPositionX());
        assertEquals(5, drone.getPositionY());
        assertTrue(drone.getBattery() < initialBattery);
    }

    @Test
    void testUpdatePosition_ShouldNotGoBelowZeroBattery() {
        drone.setBattery(0.1);

        drone.updatePosition(100, 100);

        assertEquals(0.0, drone.getBattery(), 0.01);
    }

    @Test
    void testChangeState_ShouldUpdateStateAndTimestamp() {
        LocalDateTime beforeChange = LocalDateTime.now().minusSeconds(1);

        drone.changeState(DroneState.IN_FLIGHT);

        assertEquals(DroneState.IN_FLIGHT, drone.getState());
        assertNotNull(drone.getLastStateChange());
        assertTrue(drone.getLastStateChange().isAfter(beforeChange));
    }

    @Test
    void testGetTotalWeight_ShouldReturnCorrectWeight() {
        drone.getOrderList().add(Order.builder().weight(10).build());
        drone.getOrderList().add(Order.builder().weight(15).build());

        int totalWeight = drone.getTotalWeight();

        assertEquals(25, totalWeight);
    }

    @Test
    void testGetTotalWeight_ShouldReturnZero_WhenNoOrders() {
        int totalWeight = drone.getTotalWeight();

        assertEquals(0, totalWeight);
    }

    @Test
    void testHasOrders_ShouldReturnTrue_WhenOrdersExist() {
        drone.getOrderList().add(order);

        assertTrue(drone.hasOrders());
    }

    @Test
    void testHasOrders_ShouldReturnFalse_WhenNoOrders() {
        assertFalse(drone.hasOrders());
    }
}
