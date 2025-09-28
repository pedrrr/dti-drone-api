package com.examble.drone_api.exception;

/**
 * Exceção lançada quando há problemas de validação com drones.
 * Exemplos: capacidade excedida, bateria insuficiente, estado inválido.
 */
public class DroneValidationException extends DroneSystemException {
    
    private final Long droneId;
    private final String validationField;
    
    public DroneValidationException(String message) {
        super("DRONE_VALIDATION_ERROR", message, message);
        this.droneId = null;
        this.validationField = null;
    }
    
    public DroneValidationException(String message, Long droneId) {
        super("DRONE_VALIDATION_ERROR", 
              String.format("Drone %d: %s", droneId, message), 
              message);
        this.droneId = droneId;
        this.validationField = null;
    }
    
    public DroneValidationException(String message, Long droneId, String validationField) {
        super("DRONE_VALIDATION_ERROR", 
              String.format("Drone %d - Campo %s: %s", droneId, validationField, message), 
              message);
        this.droneId = droneId;
        this.validationField = validationField;
    }
    
    public DroneValidationException(String message, Throwable cause) {
        super("DRONE_VALIDATION_ERROR", message, message, cause);
        this.droneId = null;
        this.validationField = null;
    }
    
    public Long getDroneId() {
        return droneId;
    }
    
    public String getValidationField() {
        return validationField;
    }
    
    // Factory methods para casos comuns
    public static DroneValidationException weightExceeded(Long droneId, int currentWeight, int maxWeight) {
        return new DroneValidationException(
            String.format("Peso atual (%d kg) excede limite máximo (%d kg)", currentWeight, maxWeight),
            droneId,
            "weightLimit"
        );
    }
    
    public static DroneValidationException batteryInsufficient(Long droneId, double currentBattery, double minRequired) {
        return new DroneValidationException(
            String.format("Bateria insuficiente (%.1f%%) - mínimo necessário: %.1f%%", currentBattery, minRequired),
            droneId,
            "battery"
        );
    }
    
    public static DroneValidationException notAvailableForOrders(Long droneId, String currentState) {
        return new DroneValidationException(
            String.format("Drone não disponível para pedidos - estado atual: %s", currentState),
            droneId,
            "state"
        );
    }
    
    public static DroneValidationException notAtBase(Long droneId, int currentX, int currentY) {
        return new DroneValidationException(
            String.format("Drone deve estar na base (1,1) para operações - posição atual: (%d,%d)", currentX, currentY),
            droneId,
            "position"
        );
    }
    
    public static DroneValidationException distanceExceeded(Long droneId, double distance, int maxDistance) {
        return new DroneValidationException(
            String.format("Distância (%.2f km) excede alcance máximo (%d km)", distance, maxDistance),
            droneId,
            "distance"
        );
    }
}



