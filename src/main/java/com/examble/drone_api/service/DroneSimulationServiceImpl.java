package com.examble.drone_api.service;

import com.examble.drone_api.exception.FlightSimulationException;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.repository.interfaces.DroneRepository;
import com.examble.drone_api.service.interfaces.DroneSimulationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DroneSimulationServiceImpl implements DroneSimulationService {

    private final DroneRepository droneRepository;
    
    public DroneSimulationServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    private static final int BASE_X = 1;
    private static final int BASE_Y = 1;
    private static final int DELIVERY_TIME_SECONDS = 5;
    private static final int RECHARGE_TIME_SECONDS = 10;

    @Override
    @Scheduled(fixedRate = 1000) // Executa a cada segundo
    public void updateDroneStates() {
        List<Drone> drones = droneRepository.findAll();
        
        for (Drone drone : drones) {
            updateDroneState(drone);
        }
    }

    private void updateDroneState(Drone drone) {
        switch (drone.getState()) {
            case IDLE:
                handleIdleState(drone);
                break;
            case RECHARGING:
                handleRechargingState(drone);
                break;
            case IN_FLIGHT:
                handleInFlightState(drone);
                break;
            case DELIVERING:
                handleDeliveringState(drone);
                break;
            case RETURNING_TO_BASE:
                handleReturningToBaseState(drone);
                break;
        }
    }

    private void handleIdleState(Drone drone) {
        if (drone.needsRecharging()) {
            drone.changeState(DroneState.RECHARGING);
            drone.setEstimatedArrivalTime(LocalDateTime.now().plusSeconds(RECHARGE_TIME_SECONDS));
            log.info("Drone {} iniciou recarga de bateria", drone.getId());
        }
    }

    private void handleRechargingState(Drone drone) {
        if (drone.getEstimatedArrivalTime() != null && 
            LocalDateTime.now().isAfter(drone.getEstimatedArrivalTime())) {
            drone.setBattery(100.0);
            drone.changeState(DroneState.IDLE);
            drone.setEstimatedArrivalTime(null);
            log.info("Drone {} terminou recarga e voltou ao estado IDLE", drone.getId());
        }
    }

    private void handleInFlightState(Drone drone) {
        if (drone.hasOrders()) {
            Order nextOrder = drone.getOrderList().get(0);
            
            // Simular movimento em direção ao destino
            moveTowardsDestination(drone, nextOrder.getDestinationX(), nextOrder.getDestinationY());
            
            // Se chegou ao destino
            if (drone.getPositionX() == nextOrder.getDestinationX() && 
                drone.getPositionY() == nextOrder.getDestinationY()) {
                drone.changeState(DroneState.DELIVERING);
                drone.setEstimatedArrivalTime(LocalDateTime.now().plusSeconds(DELIVERY_TIME_SECONDS));
                log.info("Drone {} chegou ao destino e iniciou entrega", drone.getId());
            }
        }
    }

    private void handleDeliveringState(Drone drone) {
        if (drone.getEstimatedArrivalTime() != null && 
            LocalDateTime.now().isAfter(drone.getEstimatedArrivalTime())) {
            
            // Remover pedido entregue
            if (drone.hasOrders()) {
                Order deliveredOrder = drone.getOrderList().remove(0);
                log.info("Drone {} entregou pedido {}", drone.getId(), deliveredOrder.getId());
                deliveredOrder.setDelivered(true);
            }
            
            // Se ainda tem pedidos, continuar para próximo destino
            if (drone.hasOrders()) {
                drone.changeState(DroneState.IN_FLIGHT);
                drone.setEstimatedArrivalTime(null);
                log.info("Drone {} iniciou voo para próximo destino", drone.getId());
            } else {
                // Sem mais pedidos, voltar para base
                drone.changeState(DroneState.RETURNING_TO_BASE);
                drone.setEstimatedArrivalTime(null);
                log.info("Drone {} iniciou retorno à base", drone.getId());
            }
        }
    }

    private void handleReturningToBaseState(Drone drone) {
        // Simular movimento em direção à base
        moveTowardsDestination(drone, BASE_X, BASE_Y);
        
        // Se chegou à base
        if (drone.getPositionX() == BASE_X && drone.getPositionY() == BASE_Y) {
            drone.changeState(DroneState.IDLE);
            log.info("Drone {} retornou à base e está IDLE", drone.getId());
        }
    }

    private void moveTowardsDestination(Drone drone, int targetX, int targetY) {
        int currentX = drone.getPositionX();
        int currentY = drone.getPositionY();
        
        // Calcular direção (movimento unitário)
        int deltaX = Integer.compare(targetX, currentX);
        int deltaY = Integer.compare(targetY, currentY);
        
        // Mover uma unidade na direção do destino
        int newX = currentX + deltaX;
        int newY = currentY + deltaY;
        
        // Atualizar posição e consumir bateria
        drone.updatePosition(newX, newY);
        
        log.debug("Drone {} moveu de ({},{}) para ({},{})", drone.getId(), currentX, currentY, newX, newY);
    }

    @Override
    @Async("droneTaskExecutor")
    public CompletableFuture<Void> startDroneFlight(Drone drone) {
        return CompletableFuture.runAsync(() -> {
            if (!drone.hasOrders()) {
                throw FlightSimulationException.noOrdersToDeliver(drone.getId());
            }
            
            if (drone.getState() != DroneState.IDLE) {
                throw FlightSimulationException.cannotStartFlight(drone.getId(), 
                    "Drone deve estar no estado IDLE para iniciar voo. Estado atual: " + drone.getState());
            }
            
            if (drone.getBattery() < 20.0) {
                throw FlightSimulationException.cannotStartFlight(drone.getId(),
                    "Bateria insuficiente para voo: " + drone.getBattery() + "%");
            }
            
            drone.changeState(DroneState.IN_FLIGHT);
            log.info("Drone {} iniciou voo com {} pedidos", drone.getId(), drone.getOrderList().size());
        });
    }

    @Override
    public void startAllDronesWithOrders() {
        List<Drone> drones = droneRepository.findAll();
        
        for (Drone drone : drones) {
            if (drone.hasOrders() && drone.getState() == DroneState.IDLE) {
                startDroneFlight(drone);
            }
        }
    }
}
