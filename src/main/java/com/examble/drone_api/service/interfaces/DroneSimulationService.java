package com.examble.drone_api.service.interfaces;

import com.examble.drone_api.model.Drone;

import java.util.concurrent.CompletableFuture;

public interface DroneSimulationService {
    void updateDroneStates();
    CompletableFuture<Void> startDroneFlight(Drone drone);
    void startAllDronesWithOrders();
}
