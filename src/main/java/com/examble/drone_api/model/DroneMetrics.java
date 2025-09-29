package com.examble.drone_api.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DroneMetrics {
    private final AtomicInteger deliveries = new AtomicInteger(0);
    private final AtomicLong totalDeliveryTime = new AtomicLong(0);
    private final AtomicLong totalFlightTime = new AtomicLong(0);
    @Getter
    private LocalDateTime firstDelivery;
    @Getter
    private LocalDateTime lastDelivery;

    public void recordDelivery(long deliveryTimeMs) {
        deliveries.incrementAndGet();
        totalDeliveryTime.addAndGet(deliveryTimeMs);

        LocalDateTime now = LocalDateTime.now();
        if (firstDelivery == null) {
            firstDelivery = now;
        }
        lastDelivery = now;
    }

    public void recordFlightTime(long flightTimeMs) {
        totalFlightTime.addAndGet(flightTimeMs);
    }

    public int getDeliveries() {
        return deliveries.get();
    }

    public double getAverageDeliveryTime() {
        int deliveriesCount = deliveries.get();
        return deliveriesCount > 0 ? (double) totalDeliveryTime.get() / deliveriesCount : 0.0;
    }

    public double getEfficiency() {
        long totalTime = totalFlightTime.get();
        return totalTime > 0 ? (double) deliveries.get() / (totalTime / 1000.0) : 0.0; // entregas por segundo
    }

}