package com.examble.drone_api.service.interfaces;

import com.examble.drone_api.model.DroneMetrics;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface DroneMetricsService {
    void recordDelivery(Long droneId, long deliveryTimeMs);
    void recordFlightTime(Long droneId, long flightTimeMs);
    Map<Long, DroneMetrics> getAllDroneMetrics();
    int getTotalDeliveries();
    double getAverageDeliveryTime();
    Long getMostEfficientDroneId();
    DroneMetrics getMostEfficientDroneMetrics();
}
