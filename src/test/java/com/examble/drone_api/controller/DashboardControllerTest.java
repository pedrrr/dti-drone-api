package com.examble.drone_api.controller;

import com.examble.drone_api.dto.DashboardResponseDTO;
import com.examble.drone_api.service.interfaces.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DroneController(null, null, dashboardService))
                .setControllerAdvice(new com.examble.drone_api.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetDashboard_ShouldReturnDashboardData() throws Exception {
        // Given
        Map<Long, DashboardResponseDTO.DroneMetricsDTO> droneMetrics = new HashMap<>();
        droneMetrics.put(1L, DashboardResponseDTO.DroneMetricsDTO.builder()
                .droneId(1L)
                .deliveries(5)
                .averageDeliveryTimeMs(2000.0)
                .efficiency(0.5)
                .firstDelivery(LocalDateTime.now().minusHours(2))
                .lastDelivery(LocalDateTime.now().minusMinutes(30))
                .build());

        DashboardResponseDTO dashboardData = DashboardResponseDTO.builder()
                .totalDeliveries(5)
                .averageDeliveryTimeMs(2000.0)
                .mostEfficientDroneId(1L)
                .mostEfficientDroneDeliveries(5)
                .mostEfficientDroneEfficiency(0.5)
                .lastUpdate(LocalDateTime.now())
                .droneMetrics(droneMetrics)
                .build();

        when(dashboardService.getDashboardData()).thenReturn(dashboardData);

        // When & Then
        mockMvc.perform(get("/api/v1/drones/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(5))
                .andExpect(jsonPath("$.averageDeliveryTimeMs").value(2000.0))
                .andExpect(jsonPath("$.mostEfficientDroneId").value(1L))
                .andExpect(jsonPath("$.mostEfficientDroneDeliveries").value(5))
                .andExpect(jsonPath("$.mostEfficientDroneEfficiency").value(0.5))
                .andExpect(jsonPath("$.lastUpdate").exists())
                .andExpect(jsonPath("$.droneMetrics").exists())
                .andExpect(jsonPath("$.droneMetrics.1.droneId").value(1L))
                .andExpect(jsonPath("$.droneMetrics.1.deliveries").value(5))
                .andExpect(jsonPath("$.droneMetrics.1.averageDeliveryTimeMs").value(2000.0))
                .andExpect(jsonPath("$.droneMetrics.1.efficiency").value(0.5));
    }

    @Test
    void testGetDashboard_ShouldReturnInternalServerError_WhenExceptionOccurs() throws Exception {
        // Given
        when(dashboardService.getDashboardData()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/drones/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
