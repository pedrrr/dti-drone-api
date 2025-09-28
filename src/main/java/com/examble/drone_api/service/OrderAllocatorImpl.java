package com.examble.drone_api.service;

import com.examble.drone_api.exception.OrderAllocationException;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import com.examble.drone_api.repository.interfaces.OrderRepository;
import com.examble.drone_api.service.interfaces.OrderAllocator;
import com.examble.drone_api.validation.DroneValidator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OrderAllocatorImpl implements OrderAllocator {

    public OrderRepository orderRepository;
    public DroneRepository droneRepository;
    public DroneValidator droneValidator;
    
    public OrderAllocatorImpl(OrderRepository orderRepository,
                              DroneRepository droneRepository,
                              DroneValidator droneValidator) {
        this.orderRepository = orderRepository;
        this.droneRepository = droneRepository;
        this.droneValidator = droneValidator;
    }

    @Override
    public void allocateOrders() {
        List<Drone> drones = droneRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        drones.stream()
                .filter(Drone::isAvailableForOrders)
                .forEach(drone -> drone.getOrderList().clear());

        orders.sort(Comparator.comparing(Order::getPriority).reversed());

        for (Order order : orders) {
            Drone bestDrone = findBestDroneForOrder(drones, order);

            if (bestDrone != null) {
                try {
                    DroneValidator.ValidationResult validation = droneValidator.validateDroneForOrder(bestDrone, order);
                    validation.throwIfInvalid();
                    
                    bestDrone.assignOrder(order);
                    System.out.println("Pedido " + order.getId() + " alocado no drone " + bestDrone.getId());
                } catch (Exception e) {
                    System.out.println("Pedido " + order.getId() + " não pôde ser alocado: " + e.getMessage());
                    if (e instanceof OrderAllocationException) {
                        System.err.println("Erro de alocação: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Pedido " + order.getId() + " não pôde ser alocado - nenhum drone disponível.");
                throw OrderAllocationException.noAvailableDrones(order.getId());
            }
        }
    }

    private Drone findBestDroneForOrder(List<Drone> drones, Order order) {
        return drones.stream()
                .filter(drone -> drone.isAvailableForOrders())
                .filter(drone -> drone.canCarry(order) && drone.canReach(order))
                .filter(drone -> drone.getBattery() >= 20.0)
                .min(Comparator
                        .<Drone>comparingDouble(drone -> calculateAllocationScore(drone, order))
                        .thenComparingDouble(drone -> drone.calculateDistance(order))
                )
                .orElse(null);
    }

    private double calculateAllocationScore(Drone drone, Order order) {
        double distance = drone.calculateDistance(order);
        double weightUtilization = (double) drone.getTotalWeight() / drone.getWeightLimit();
        double batteryLevel = drone.getBattery() / 100.0;

        return distance + (1.0 - batteryLevel) * 10 + weightUtilization * 5;
    }
}
