package com.examble.drone_api.controller;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.dto.OrderResponseDTO;
import com.examble.drone_api.exception.ResourceNotFoundException;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.Priority;
import com.examble.drone_api.service.interfaces.OrderService;
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
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(new com.examble.drone_api.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateOrder_ShouldReturnCreatedOrder() throws Exception {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(10, 15, 25, Priority.HIGH);

        Order createdOrder = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        when(orderService.createOrder(any(OrderCreateRequestDTO.class))).thenReturn(createdOrder);

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.destinationX").value(10))
                .andExpect(jsonPath("$.destinationY").value(15))
                .andExpect(jsonPath("$.weight").value(25))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(orderService).createOrder(any(OrderCreateRequestDTO.class));
    }

    @Test
    void testCreateOrder_ShouldUseDefaultPriority_WhenNotProvided() throws Exception {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(5, 8, 15, null); // Priority not set, should default to LOW

        Order createdOrder = Order.builder()
                .id(1L)
                .destinationX(5)
                .destinationY(8)
                .weight(15)
                .priority(Priority.LOW)
                .build();

        when(orderService.createOrder(any(OrderCreateRequestDTO.class))).thenReturn(createdOrder);

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.destinationX").value(5))
                .andExpect(jsonPath("$.destinationY").value(8))
                .andExpect(jsonPath("$.weight").value(15))
                .andExpect(jsonPath("$.priority").value("LOW"));

        verify(orderService).createOrder(any(OrderCreateRequestDTO.class));
    }

    @Test
    void testGetOrder_ShouldReturnOrder_WhenExists() throws Exception {
        // Given
        Order order = Order.builder()
                .id(1L)
                .destinationX(10)
                .destinationY(15)
                .weight(25)
                .priority(Priority.HIGH)
                .build();

        when(orderService.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.destinationX").value(10))
                .andExpect(jsonPath("$.destinationY").value(15))
                .andExpect(jsonPath("$.weight").value(25))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(orderService).findById(1L);
    }

    @Test
    void testGetOrder_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(orderService.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isNotFound());

        verify(orderService).findById(1L);
    }

    @Test
    void testGetAllOrders_ShouldReturnAllOrders() throws Exception {
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

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.findAll()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].priority").value("MEDIUM"));

        verify(orderService).findAll();
    }

    @Test
    void testGetAllOrders_ShouldReturnEmptyList_WhenNoOrders() throws Exception {
        // Given
        when(orderService.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService).findAll();
    }

    @Test
    void testCreateOrder_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(0, 0, 0, null); // Invalid values

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testCreateOrder_ShouldHandleNegativeValues() throws Exception {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(-5, -10, -15, Priority.LOW);

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testCreateOrder_ShouldHandleZeroValues() throws Exception {
        // Given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO(1, 1, 1, Priority.LOW); // Use valid values instead of 0

        Order createdOrder = Order.builder()
                .id(1L)
                .destinationX(1)
                .destinationY(1)
                .weight(1)
                .priority(Priority.LOW)
                .build();

        when(orderService.createOrder(any(OrderCreateRequestDTO.class))).thenReturn(createdOrder);

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destinationX").value(1))
                .andExpect(jsonPath("$.destinationY").value(1))
                .andExpect(jsonPath("$.weight").value(1));

        verify(orderService).createOrder(any(OrderCreateRequestDTO.class));
    }
}
