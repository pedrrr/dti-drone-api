package com.examble.drone_api.validation;

import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import org.springframework.stereotype.Component;

@Component
public class DroneValidator {

    public ValidationResult validateDroneForOrder(Drone drone, Order order) {
        ValidationResult result = new ValidationResult();
        
        // Verificar se drone está disponível para pedidos
        if (!drone.isAvailableForOrders()) {
            result.addError("Drone não está disponível para pedidos. Estado atual: " + drone.getState());
        }
        
        // Verificar capacidade de peso
        if (!drone.canCarry(order)) {
            int totalWeight = drone.getTotalWeight();
            int availableWeight = drone.getWeightLimit() - totalWeight;
            result.addError(String.format("Peso do pedido (%d kg) excede capacidade disponível (%d kg)", 
                order.getWeight(), availableWeight));
        }
        
        // Verificar alcance
        if (!drone.canReach(order)) {
            double distance = drone.calculateDistance(order);
            result.addError(String.format("Distância do pedido (%.2f km) excede alcance máximo (%d km)", 
                distance, drone.getDistancePerCargo()));
        }
        
        // Verificar bateria mínima
        if (drone.getBattery() < 20.0) {
            result.addError(String.format("Bateria insuficiente para voo (%.1f%%). Mínimo necessário: 20%%", 
                drone.getBattery()));
        }
        
        // Verificar se está na base
        if (!drone.isAtBase()) {
            result.addError("Drone deve estar na base (1,1) para receber novos pedidos");
        }
        
        return result;
    }
    
    public ValidationResult validateDroneForFlight(Drone drone) {
        ValidationResult result = new ValidationResult();
        
        // Verificar se tem pedidos
        if (!drone.hasOrders()) {
            result.addError("Drone não possui pedidos para entregar");
        }
        
        // Verificar estado
        if (drone.getState() != com.examble.drone_api.model.type.DroneState.IDLE) {
            result.addError("Drone deve estar no estado IDLE para iniciar voo. Estado atual: " + drone.getState());
        }
        
        // Verificar bateria
        if (drone.getBattery() < 20.0) {
            result.addError(String.format("Bateria insuficiente para voo (%.1f%%)", drone.getBattery()));
        }
        
        // Verificar se está na base
        if (!drone.isAtBase()) {
            result.addError("Drone deve estar na base para iniciar voo");
        }
        
        return result;
    }
    
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errors.toString();
        }
        
        public void throwIfInvalid() {
            if (!valid) {
                throw new IllegalArgumentException("Validação falhou: " + getErrorMessage());
            }
        }
    }
}

