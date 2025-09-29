package com.examble.drone_api.service;

import com.examble.drone_api.dto.DashboardResponseDTO;
import com.examble.drone_api.model.DroneMetrics;
import com.examble.drone_api.service.interfaces.DashboardService;
import com.examble.drone_api.service.interfaces.DroneMetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    
    private final DroneMetricsService metricsService;
    
    public DashboardServiceImpl(DroneMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public DashboardResponseDTO getDashboardData() {
        log.info("Gerando dados do dashboard");

        // Obter métricas globais
        int totalDeliveries = metricsService.getTotalDeliveries();
        double averageDeliveryTime = metricsService.getAverageDeliveryTime();

        // Obter drone mais eficiente
        Long mostEfficientDroneId = metricsService.getMostEfficientDroneId();
        DroneMetrics mostEfficientMetrics = metricsService.getMostEfficientDroneMetrics();

        // Obter métricas de todos os drones
        Map<Long,DroneMetrics> allMetrics = metricsService.getAllDroneMetrics();

        // Converter para DTOs
        Map<Long, DashboardResponseDTO.DroneMetricsDTO> droneMetricsDTOs = allMetrics.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> DashboardResponseDTO.DroneMetricsDTO.builder()
                            .droneId(entry.getKey())
                            .deliveries(entry.getValue().getDeliveries())
                            .averageDeliveryTimeMs(entry.getValue().getAverageDeliveryTime())
                            .efficiency(entry.getValue().getEfficiency())
                            .firstDelivery(entry.getValue().getFirstDelivery())
                            .lastDelivery(entry.getValue().getLastDelivery())
                            .build()
                ));

        return DashboardResponseDTO.builder()
                .totalDeliveries(totalDeliveries)
                .averageDeliveryTimeMs(averageDeliveryTime)
                .mostEfficientDroneId(mostEfficientDroneId)
                .mostEfficientDroneDeliveries(mostEfficientMetrics != null ? mostEfficientMetrics.getDeliveries() : 0)
                .mostEfficientDroneEfficiency(mostEfficientMetrics != null ? mostEfficientMetrics.getEfficiency() : 0.0)
                .lastUpdate(LocalDateTime.now())
                .droneMetrics(droneMetricsDTOs)
                .build();
    }
}
