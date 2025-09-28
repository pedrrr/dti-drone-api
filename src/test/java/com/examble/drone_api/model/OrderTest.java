package com.examble.drone_api.model;

import com.examble.drone_api.model.type.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderBuilder_ShouldCreateOrderWithDefaultPriority() {
        Order order = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .build();

        assertEquals(1L, order.getId());
        assertEquals(10, order.getDestinationX());
        assertEquals(15, order.getDestinationY());
        assertEquals(25, order.getWeight());
        assertEquals(Priority.LOW, order.getPriority()); // Default value
    }

    @Test
    void testOrderBuilder_ShouldCreateOrderWithCustomPriority() {
        Order order = Order.builder()
                .id(2L)
                .destinationX(5)
                .destinationY(8)
                .weight(15)
                .priority(Priority.HIGH)
                .build();

        assertEquals(2L, order.getId());
        assertEquals(5, order.getDestinationX());
        assertEquals(8, order.getDestinationY());
        assertEquals(15, order.getWeight());
        assertEquals(Priority.HIGH, order.getPriority());
    }

    @Test
    void testOrderNoArgsConstructor_ShouldCreateEmptyOrder() {
        Order order = new Order();

        assertNull(order.getId());
        assertEquals(0, order.getDestinationX());
        assertEquals(0, order.getDestinationY());
        assertEquals(0, order.getWeight());
        assertEquals(Priority.LOW, order.getPriority()); // Default value
    }

    @Test
    void testOrderAllArgsConstructor_ShouldCreateOrderWithAllFields() {
        Order order = new Order(1L, 20, 25, 30, Priority.MEDIUM);

        assertEquals(1L, order.getId());
        assertEquals(20, order.getDestinationX());
        assertEquals(25, order.getDestinationY());
        assertEquals(30, order.getWeight());
        assertEquals(Priority.MEDIUM, order.getPriority());
    }

    @Test
    void testOrderSetters_ShouldUpdateFields() {

        Order order = new Order();

        order.setId(3L);
        order.setDestinationX(12);
        order.setDestinationY(18);
        order.setWeight(22);
        order.setPriority(Priority.HIGH);

        assertEquals(3L, order.getId());
        assertEquals(12, order.getDestinationX());
        assertEquals(18, order.getDestinationY());
        assertEquals(22, order.getWeight());
        assertEquals(Priority.HIGH, order.getPriority());
    }

    @Test
    void testOrderEquals_ShouldReturnTrue_WhenSameId() {

        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();

        assertEquals(order1, order2);
    }

    @Test
    void testOrderEquals_ShouldReturnFalse_WhenDifferentId() {

        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(2L).destinationX(5).destinationY(5).weight(10).build();

        assertNotEquals(order1, order2);
    }

    @Test
    void testOrderHashCode_ShouldReturnSameValue_WhenSameId() {

        Order order1 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();
        Order order2 = Order.builder().id(1L).destinationX(5).destinationY(5).weight(10).build();

        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testOrderToString_ShouldContainAllFields() {

        Order order = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(20)
                .weight(30)
                .priority(Priority.HIGH)
                .build();

        String orderString = order.toString();

        assertTrue(orderString.contains("id=1"));
        assertTrue(orderString.contains("destinationX=10"));
        assertTrue(orderString.contains("destinationY=20"));
        assertTrue(orderString.contains("weight=30"));
        assertTrue(orderString.contains("priority=HIGH"));
    }
}



