package com.examble.drone_api.exception;

/**
 * Exceção lançada quando há problemas na simulação de voos dos drones.
 * Exemplos: falha no movimento, erro de navegação, problema de bateria.
 */
public class FlightSimulationException extends DroneSystemException {
    
    private final Long droneId;
    private final String simulationPhase;
    private final String flightOperation;
    
    public FlightSimulationException(String message) {
        super("FLIGHT_SIMULATION_ERROR", message, message);
        this.droneId = null;
        this.simulationPhase = null;
        this.flightOperation = null;
    }
    
    public FlightSimulationException(String message, Long droneId) {
        super("FLIGHT_SIMULATION_ERROR", 
              String.format("Drone %d: %s", droneId, message), 
              message);
        this.droneId = droneId;
        this.simulationPhase = null;
        this.flightOperation = null;
    }
    
    public FlightSimulationException(String message, Long droneId, String simulationPhase) {
        super("FLIGHT_SIMULATION_ERROR", 
              String.format("Drone %d - Fase %s: %s", droneId, simulationPhase, message), 
              message);
        this.droneId = droneId;
        this.simulationPhase = simulationPhase;
        this.flightOperation = null;
    }
    
    public FlightSimulationException(String message, Long droneId, String simulationPhase, String flightOperation) {
        super("FLIGHT_SIMULATION_ERROR", 
              String.format("Drone %d - %s em %s: %s", droneId, flightOperation, simulationPhase, message), 
              message);
        this.droneId = droneId;
        this.simulationPhase = simulationPhase;
        this.flightOperation = flightOperation;
    }
    
    public Long getDroneId() {
        return droneId;
    }
    
    public String getSimulationPhase() {
        return simulationPhase;
    }
    
    public String getFlightOperation() {
        return flightOperation;
    }
    
    // Factory methods para casos comuns
    public static FlightSimulationException cannotStartFlight(Long droneId, String reason) {
        return new FlightSimulationException(
            String.format("Não é possível iniciar voo: %s", reason),
            droneId,
            "INICIAR_VOO",
            "startFlight"
        );
    }
    
    public static FlightSimulationException batteryDepleted(Long droneId, double currentBattery) {
        return new FlightSimulationException(
            String.format("Bateria esgotada durante voo (%.1f%%)", currentBattery),
            droneId,
            "EM_VOO",
            "movement"
        );
    }
    
    public static FlightSimulationException navigationError(Long droneId, int fromX, int fromY, int toX, int toY) {
        return new FlightSimulationException(
            String.format("Erro de navegação de (%d,%d) para (%d,%d)", fromX, fromY, toX, toY),
            droneId,
            "NAVEGACAO",
            "movement"
        );
    }
    
    public static FlightSimulationException deliveryFailed(Long droneId, Long orderId, String reason) {
        return new FlightSimulationException(
            String.format("Falha na entrega do pedido %d: %s", orderId, reason),
            droneId,
            "ENTREGA",
            "delivery"
        );
    }
    
    public static FlightSimulationException returnToBaseFailed(Long droneId, String reason) {
        return new FlightSimulationException(
            String.format("Falha no retorno à base: %s", reason),
            droneId,
            "RETORNO_BASE",
            "returnToBase"
        );
    }
    
    public static FlightSimulationException rechargingError(Long droneId, String reason) {
        return new FlightSimulationException(
            String.format("Erro na recarga: %s", reason),
            droneId,
            "RECARGA",
            "recharging"
        );
    }
    
    public static FlightSimulationException stateTransitionError(Long droneId, String currentState, String targetState) {
        return new FlightSimulationException(
            String.format("Erro na transição de estado de %s para %s", currentState, targetState),
            droneId,
            "TRANSICAO_ESTADO",
            "stateChange"
        );
    }
    
    public static FlightSimulationException noOrdersToDeliver(Long droneId) {
        return new FlightSimulationException(
            "Drone não possui pedidos para entregar",
            droneId,
            "INICIAR_VOO",
            "startFlight"
        );
    }
    
    public static FlightSimulationException invalidPosition(Long droneId, int x, int y) {
        return new FlightSimulationException(
            String.format("Posição inválida: (%d,%d)", x, y),
            droneId,
            "POSICIONAMENTO",
            "positioning"
        );
    }
}




