package com.examble.drone_api.exception;

/**
 * Exceção lançada quando há problemas na alocação de pedidos para drones.
 * Exemplos: nenhum drone disponível, falha na otimização, pedido não alocável.
 */
public class OrderAllocationException extends DroneSystemException {
    
    private final Long orderId;
    private final Long droneId;
    private final String allocationReason;
    
    public OrderAllocationException(String message) {
        super("ORDER_ALLOCATION_ERROR", message, message);
        this.orderId = null;
        this.droneId = null;
        this.allocationReason = null;
    }
    
    public OrderAllocationException(String message, Long orderId) {
        super("ORDER_ALLOCATION_ERROR", 
              String.format("Pedido %d: %s", orderId, message), 
              message);
        this.orderId = orderId;
        this.droneId = null;
        this.allocationReason = null;
    }
    
    public OrderAllocationException(String message, Long orderId, Long droneId) {
        super("ORDER_ALLOCATION_ERROR", 
              String.format("Pedido %d para Drone %d: %s", orderId, droneId, message), 
              message);
        this.orderId = orderId;
        this.droneId = droneId;
        this.allocationReason = null;
    }
    
    public OrderAllocationException(String message, Long orderId, Long droneId, String allocationReason) {
        super("ORDER_ALLOCATION_ERROR", 
              String.format("Pedido %d para Drone %d - %s: %s", orderId, droneId, allocationReason, message), 
              message);
        this.orderId = orderId;
        this.droneId = droneId;
        this.allocationReason = allocationReason;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public Long getDroneId() {
        return droneId;
    }
    
    public String getAllocationReason() {
        return allocationReason;
    }
    
    // Factory methods para casos comuns
    public static OrderAllocationException noAvailableDrones(Long orderId) {
        return new OrderAllocationException(
            "Nenhum drone disponível para alocação",
            orderId
        );
    }
    
    public static OrderAllocationException weightExceedsCapacity(Long orderId, Long droneId, int orderWeight, int availableCapacity) {
        return new OrderAllocationException(
            String.format("Peso do pedido (%d kg) excede capacidade disponível (%d kg)", orderWeight, availableCapacity),
            orderId,
            droneId,
            "capacidade insuficiente"
        );
    }
    
    public static OrderAllocationException distanceExceedsRange(Long orderId, Long droneId, double distance, int maxRange) {
        return new OrderAllocationException(
            String.format("Distância (%.2f km) excede alcance do drone (%d km)", distance, maxRange),
            orderId,
            droneId,
            "alcance insuficiente"
        );
    }
    
    public static OrderAllocationException batteryInsufficient(Long orderId, Long droneId, double battery, double minRequired) {
        return new OrderAllocationException(
            String.format("Bateria insuficiente (%.1f%%) - mínimo para voo: %.1f%%", battery, minRequired),
            orderId,
            droneId,
            "bateria insuficiente"
        );
    }
    
    public static OrderAllocationException droneNotAvailable(Long orderId, Long droneId, String currentState) {
        return new OrderAllocationException(
            String.format("Drone não disponível - estado atual: %s", currentState),
            orderId,
            droneId,
            "drone indisponível"
        );
    }
    
    public static OrderAllocationException allocationOptimizationFailed(Long orderId, String reason) {
        return new OrderAllocationException(
            String.format("Falha na otimização de alocação: %s", reason),
            orderId,
            null,
            "otimização falhou"
        );
    }
}




