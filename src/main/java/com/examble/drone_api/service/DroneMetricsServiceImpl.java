package com.examble.drone_api.service;

import com.examble.drone_api.model.DroneMetrics;
import com.examble.drone_api.service.interfaces.DroneMetricsService;
import com.examble.drone_api.service.interfaces.DroneSimulationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

@Service
@Slf4j
public class DroneMetricsServiceImpl implements DroneMetricsService {

    private final Map<Long, DroneMetrics> droneMetrics = new ConcurrentHashMap<>();
    private final AtomicInteger totalDeliveries = new AtomicInteger(0);
    private final AtomicLong totalDeliveryTime = new AtomicLong(0);

    @Override
    public void recordDelivery(Long droneId, long deliveryTimeMs) {
        droneMetrics.computeIfAbsent(droneId, k -> new DroneMetrics())
                   .recordDelivery(deliveryTimeMs);
        
        totalDeliveries.incrementAndGet();
        totalDeliveryTime.addAndGet(deliveryTimeMs);
        
        log.info("Métrica registrada: Drone {} completou entrega em {}ms", droneId, deliveryTimeMs);
    }

    @Override
    public void recordFlightTime(Long droneId, long flightTimeMs) {
        droneMetrics.computeIfAbsent(droneId, k -> new DroneMetrics())
                   .recordFlightTime(flightTimeMs);
    }

    @Override
    public Map<Long, DroneMetrics> getAllDroneMetrics() {
        return new ConcurrentHashMap<>(droneMetrics);
    }

    @Override
    public int getTotalDeliveries() {
        return totalDeliveries.get();
    }

    @Override
    public double getAverageDeliveryTime() {
        int totalDeliveriesCount = totalDeliveries.get();
        return totalDeliveriesCount > 0 ? (double) totalDeliveryTime.get() / totalDeliveriesCount : 0.0;
    }

    @Override
    public Long getMostEfficientDroneId() {
        return droneMetrics.entrySet().stream()
                .max(Comparator.comparingDouble(entry -> entry.getValue().getEfficiency()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public DroneMetrics getMostEfficientDroneMetrics() {
        Long mostEfficientId = getMostEfficientDroneId();
        return mostEfficientId != null ? droneMetrics.get(mostEfficientId) : new DroneMetrics();
    }
}
