package com.examble.drone_api.validation;

import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.model.type.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DroneValidatorTest {

    private DroneValidator droneValidator;
    private Drone drone;
    private Order order;

    @BeforeEach
    void setUp() {
        droneValidator = new DroneValidator();
        
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
                .priority(Priority.HIGH)
                .build();
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnValid_WhenAllConditionsMet() {
        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertTrue(result.isValid());
        assertTrue(result.getErrorMessage().isEmpty());
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenDroneNotAvailable() {
        drone.setState(DroneState.IN_FLIGHT);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("não está disponível para pedidos"));
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenWeightExceeded() {
        drone.getOrderList().add(Order.builder().weight(45).build());
        order.setWeight(10);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Peso do pedido"));
        assertTrue(result.getErrorMessage().contains("excede capacidade disponível"));
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenDistanceExceeded() {
        order.setDestinationX(100);
        order.setDestinationY(100);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Distância do pedido"));
        assertTrue(result.getErrorMessage().contains("excede alcance máximo"));
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenBatteryLow() {
        drone.setBattery(15.0);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Bateria insuficiente para voo"));
        assertTrue(result.getErrorMessage().contains("15"));
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenNotAtBase() {
        drone.setPositionX(5);
        drone.setPositionY(5);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("deve estar na base"));
    }

    @Test
    void testValidateDroneForOrder_ShouldReturnInvalid_WhenMultipleErrors() {
        drone.setState(DroneState.IN_FLIGHT);
        drone.setBattery(15.0);
        drone.setPositionX(5);
        drone.setPositionY(5);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        String errorMessage = result.getErrorMessage();
        assertTrue(errorMessage.contains("não está disponível para pedidos"));
        assertTrue(errorMessage.contains("Bateria insuficiente"));
        assertTrue(errorMessage.contains("deve estar na base"));
    }

    @Test
    void testValidateDroneForFlight_ShouldReturnValid_WhenAllConditionsMet() {
        drone.getOrderList().add(order);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForFlight(drone);

        assertTrue(result.isValid());
        assertTrue(result.getErrorMessage().isEmpty());
    }

    @Test
    void testValidateDroneForFlight_ShouldReturnInvalid_WhenNoOrders() {
        DroneValidator.ValidationResult result = droneValidator.validateDroneForFlight(drone);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("não possui pedidos para entregar"));
    }

    @Test
    void testValidateDroneForFlight_ShouldReturnInvalid_WhenNotIdle() {
        drone.getOrderList().add(order);
        drone.setState(DroneState.IN_FLIGHT);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForFlight(drone);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("deve estar no estado IDLE"));
    }

    @Test
    void testValidateDroneForFlight_ShouldReturnInvalid_WhenBatteryLow() {
        drone.getOrderList().add(order);
        drone.setBattery(15.0);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForFlight(drone);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Bateria insuficiente para voo"));
    }

    @Test
    void testValidateDroneForFlight_ShouldReturnInvalid_WhenNotAtBase() {
        drone.getOrderList().add(order);
        drone.setPositionX(5);
        drone.setPositionY(5);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForFlight(drone);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("deve estar na base para iniciar voo"));
    }

    @Test
    void testValidationResult_ShouldThrowException_WhenInvalid() {
        DroneValidator.ValidationResult result = new DroneValidator.ValidationResult();
        result.addError("Test error");
        assertThrows(IllegalArgumentException.class, result::throwIfInvalid);
    }

    @Test
    void testValidationResult_ShouldNotThrowException_WhenValid() {
        DroneValidator.ValidationResult result = new DroneValidator.ValidationResult();
        assertDoesNotThrow(result::throwIfInvalid);
    }

    @Test
    void testValidationResult_ShouldAccumulateErrors() {
        DroneValidator.ValidationResult result = new DroneValidator.ValidationResult();

        result.addError("Error 1");
        result.addError("Error 2");
        result.addError("Error 3");

        assertFalse(result.isValid());
        String errorMessage = result.getErrorMessage();
        assertTrue(errorMessage.contains("Error 1"));
        assertTrue(errorMessage.contains("Error 2"));
        assertTrue(errorMessage.contains("Error 3"));
        assertTrue(errorMessage.contains("; "));
    }

    @Test
    void testValidateDroneForOrder_ShouldHandleEdgeCase_BatteryExactly20() {
        drone.setBattery(20.0);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertTrue(result.isValid());
    }

    @Test
    void testValidateDroneForOrder_ShouldHandleEdgeCase_BatteryJustBelow20() {
        drone.setBattery(19.9);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Bateria insuficiente"));
    }

    @Test
    void testValidateDroneForOrder_ShouldHandleEdgeCase_ExactWeightLimit() {
        order.setWeight(50);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);

        assertTrue(result.isValid());
    }

    @Test
    void testValidateDroneForOrder_ShouldHandleEdgeCase_ExactDistanceLimit() {
        order.setDestinationX(21);
        order.setDestinationY(1);

        DroneValidator.ValidationResult result = droneValidator.validateDroneForOrder(drone, order);
        assertTrue(result.isValid());
    }
}
