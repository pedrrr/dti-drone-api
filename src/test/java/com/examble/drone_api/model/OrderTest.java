package com.examble.drone_api.model;

import com.examble.drone_api.model.type.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderBuilder_ShouldCreateOrderWithDefaultPriority() {
        // When
        Order order = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .build();

        // Then
        assertEquals(1L, order.getId());
        assertEquals(10, order.getDestinationX());
        assertEquals(15, order.getDestinationY());
        assertEquals(25, order.getWeight());
        assertEquals(Priority.LOW, order.getPriority()); // Default value
    }

    @Test
    void testOrderBuilder_ShouldCreateOrderWithCustomPriority() {
        // When
        Order order = Order.builder()
                .id(2L)
                .destinationX(5)
                .destinationY(8)
                .weight(15)
                .priority(Priority.HIGH)
                .build();

        // Then
        assertEquals(2L, order.getId());
        assertEquals(5, order.getDestinationX());
        assertEquals(8, order.getDestinationY());
        assertEquals(15, order.getWeight());
        assertEquals(Priority.HIGH, order.getPriority());
    }

    @Test
    void testOrderNoArgsConstructor_ShouldCreateEmptyOrder() {
        // When
        Order order = new Order();

        // Then
        assertNull(order.getId());
        assertEquals(0, order.getDestinationX());
        assertEquals(0, order.getDestinationY());
        assertEquals(0, order.getWeight());
        assertEquals(Priority.LOW, order.getPriority()); // Default value
    }

    @Test
    void testOrderAllArgsConstructor_ShouldCreateOrderWithAllFields() {
        // When
        Order order = new Order(1L, 20, 25, 30, Priority.MEDIUM, false);

        // Then
        assertEquals(1L, order.getId());
        assertEquals(20, order.getDestinationX());
        assertEquals(25, order.getDestinationY());
        assertEquals(30, order.getWeight());
        assertEquals(Priority.MEDIUM, order.getPriority());
    }

    @Test
    void testOrderSetters_ShouldUpdateFields() {
        // Given
        Order order = new Order();

        // When
        order.setId(3L);
        order.setDestinationX(12);
        order.setDestinationY(18);
        order.setWeight(22);
        order.setPriority(Priority.HIGH);

        // Then
        assertEquals(3L, order.getId());
        assertEquals(12, order.getDestinationX());
        assertEquals(18, order.getDestinationY());
        assertEquals(22, order.getWeight());
        assertEquals(Priority.HIGH, order.getPriority());
    }

    @Test
    void testOrderEquals_ShouldReturnTrue_WhenSameId() {
        // Given
        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();

        // When & Then
        assertEquals(order1, order2);
    }

    @Test
    void testOrderEquals_ShouldReturnFalse_WhenDifferentId() {
        // Given
        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(2L).destinationX(5).destinationY(5).weight(10).build();

        // When & Then
        assertNotEquals(order1, order2);
    }

    @Test
    void testOrderHashCode_ShouldReturnSameValue_WhenSameId() {
        // Given
        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();

        // When & Then
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testOrderToString_ShouldContainAllFields() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(20)
                .weight(30)
                .priority(Priority.HIGH)
                .build();

        // When
        String orderString = order.toString();

        // Then
        assertTrue(orderString.contains("id=1"));
        assertTrue(orderString.contains("destinationX=10"));
        assertTrue(orderString.contains("destinationY=20"));
        assertTrue(orderString.contains("weight=30"));
        assertTrue(orderString.contains("priority=HIGH"));
    }
}




