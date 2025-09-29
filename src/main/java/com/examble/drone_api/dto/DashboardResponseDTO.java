package com.examble.drone_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    private int totalDeliveries;
    private double averageDeliveryTimeMs;
    private Long mostEfficientDroneId;
    private int mostEfficientDroneDeliveries;
    private double mostEfficientDroneEfficiency;
    private LocalDateTime lastUpdate;
    
    // Métricas detalhadas por drone
    private java.util.Map<Long, DroneMetricsDTO> droneMetrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DroneMetricsDTO {
        private Long droneId;
        private int deliveries;
        private double averageDeliveryTimeMs;
        private double efficiency;
        private LocalDateTime firstDelivery;
        private LocalDateTime lastDelivery;
    }
}
