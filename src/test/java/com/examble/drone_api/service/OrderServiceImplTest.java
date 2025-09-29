package com.examble.drone_api.service;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.Priority;
import com.examble.drone_api.repository.interfaces.OrderRepository;
import com.examble.drone_api.service.interfaces.OrderAllocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderAllocator orderAllocator;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderAllocator);
    }

    @Test
    void testFindAll_ShouldReturnAllOrders() {
        // Given
        Order order1 = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .destinationX(5)
                .destinationY(8)
                .weight(15)
                .priority(Priority.MEDIUM)
                .build();

        List<Order> expectedOrders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        List<Order> result = orderService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedOrders, result);
        verify(orderRepository).findAll();
    }

    @Test
    void testFindAll_ShouldReturnEmptyList_WhenNoOrders() {
        // Given
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Order> result = orderService.findAll();

        // Then
        assertTrue(result.isEmpty());
        verify(orderRepository).findAll();
    }

    @Test
    void testFindById_ShouldReturnOrder_WhenExists() {
        // Given
        Order expectedOrder = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(expectedOrder));

        // When
        Optional<Order> result = orderService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedOrder, result.get());
        verify(orderRepository).findById(1L);
    }

    @Test
    void testFindById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.findById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(orderRepository).findById(1L);
    }

    @Test
    void testCreateOrder_ShouldCreateOrderAndTriggerAllocation() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(10, 15, 25, Priority.HIGH);

        Order savedOrder = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderAllocator).allocateOrders();

        // When
        Order result = orderService.createOrder(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10, result.getDestinationX());
        assertEquals(15, result.getDestinationY());
        assertEquals(25, result.getWeight());
        assertEquals(Priority.HIGH, result.getPriority());

        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator).allocateOrders();
    }

    @Test
    void testCreateOrder_ShouldUseDefaultPriority_WhenNotProvided() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(5, 8, 15, null); // Priority not set, should default to LOW

        Order savedOrder = Order.builder()
                .id(1L)
                .destinationX(5)
                .destinationY(8)
                .weight(15)
                .priority(Priority.LOW)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderAllocator).allocateOrders();

        // When
        Order result = orderService.createOrder(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(Priority.LOW, result.getPriority());

        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator).allocateOrders();
    }

    @Test
    void testCreateOrder_ShouldHandleRepositoryException() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(10, 15, 25, Priority.HIGH);

        when(orderRepository.save(any(Order.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(requestDTO));
        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator, never()).allocateOrders();
    }

    @Test
    void testCreateOrder_ShouldHandleAllocationException() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(10, 15, 25, Priority.HIGH);

        Order savedOrder = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doThrow(new RuntimeException("Allocation error")).when(orderAllocator).allocateOrders();

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(requestDTO));
        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator).allocateOrders();
    }

    @Test
    void testCreateOrder_ShouldPreserveRequestData() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(20, 30, 40, Priority.MEDIUM);

        Order savedOrder = Order.builder()
                .id(1L)
                .destinationX(20)
                .destinationY(30)
                .weight(40)
                .priority(Priority.MEDIUM)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderAllocator).allocateOrders();

        // When
        Order result = orderService.createOrder(requestDTO);

        // Then
        assertEquals(20, result.getDestinationX());
        assertEquals(30, result.getDestinationY());
        assertEquals(40, result.getWeight());
        assertEquals(Priority.MEDIUM, result.getPriority());

        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator).allocateOrders();
    }

    @Test
    void testCreateOrder_ShouldHandleZeroValues() {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(0, 0, 0, Priority.LOW);

        Order savedOrder = Order.builder()
                .id(1L)
                .destinationX(0)
                .destinationY(0)
                .weight(0)
                .priority(Priority.LOW)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderAllocator).allocateOrders();

        // When
        Order result = orderService.createOrder(requestDTO);

        // Then
        assertEquals(0, result.getDestinationX());
        assertEquals(0, result.getDestinationY());
        assertEquals(0, result.getWeight());
        assertEquals(Priority.LOW, result.getPriority());

        verify(orderRepository).save(any(Order.class));
        verify(orderAllocator).allocateOrders();
    }
}
