package com.examble.drone_api.model;

import com.examble.drone_api.exception.DroneValidationException;
import com.examble.drone_api.model.type.DroneState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Builder
public class Drone {
    private Long id;
    private int positionX, positionY;
    private int weightLimit;
    private int distancePerCargo;
    private double battery;
    private DroneState state;
    private LocalDateTime lastStateChange;
    private LocalDateTime estimatedArrivalTime;
    private List<Order> orderList;

    public void addCargo(Order order){
        if(orderList ==null){
            orderList = new ArrayList<>();
        }
        orderList.add(order);
    }

    public boolean canCarry(Order order) {
        int totalWeight = orderList.stream().mapToInt(Order::getWeight).sum();
        return (totalWeight + order.getWeight()) <= weightLimit;
    }

    public double calculateDistance(Order order) {
        return Math.sqrt(
                Math.pow(order.getDestinationX() - positionX, 2) +
                        Math.pow(order.getDestinationY() - positionY, 2)
        );
    }

    public boolean canReach(Order order) {
        return calculateDistance(order) <= distancePerCargo;
    }

    public void assignOrder(Order order) {
        if (!canCarry(order)) {
            int currentWeight = getTotalWeight();
            int maxWeight = weightLimit;
            throw DroneValidationException.weightExceeded(id, currentWeight + order.getWeight(), maxWeight);
        }
        if (!canReach(order)) {
            double distance = calculateDistance(order);
            throw DroneValidationException.distanceExceeded(id, distance, distancePerCargo);
        }
        if (!isAvailableForOrders()) {
            throw DroneValidationException.notAvailableForOrders(id, state.toString());
        }
        
        orderList.add(order);
    }

    public boolean isAvailableForOrders() {
        return state == DroneState.IDLE && battery >= 20.0;
    }

    public boolean needsRecharging() {
        return state == DroneState.IDLE && battery < 80.0 && isAtBase();
    }

    public boolean isAtBase() {
        return positionX == 1 && positionY == 1;
    }

    public double calculateBatteryConsumption(double distance) {
        return distance * 0.1; // 0.1% de bateria por unidade de distância
    }

    public void updatePosition(int newX, int newY) {
        double distance = Math.sqrt(Math.pow(newX - positionX, 2) + Math.pow(newY - positionY, 2));
        battery -= calculateBatteryConsumption(distance);
        if (battery < 0) battery = 0;
        
        this.positionX = newX;
        this.positionY = newY;
    }

    public void changeState(DroneState newState) {
        this.state = newState;
        this.lastStateChange = LocalDateTime.now();
    }

    public int getTotalWeight() {
        return orderList != null ? orderList.stream().mapToInt(Order::getWeight).sum() : 0;
    }

    public boolean hasOrders() {
        return orderList != null && !orderList.isEmpty();
    }
}
