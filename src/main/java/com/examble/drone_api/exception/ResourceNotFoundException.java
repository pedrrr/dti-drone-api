package com.examble.drone_api.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * Exemplos: drone não encontrado, pedido não encontrado.
 */
public class ResourceNotFoundException extends DroneSystemException {
    
    private final String resourceType;
    private final Object resourceId;
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, message);
        this.resourceType = null;
        this.resourceId = null;
    }
    
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s com ID %s não encontrado", resourceType, resourceId), 
              String.format("%s não encontrado", resourceType));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s com ID %s: %s", resourceType, resourceId, message), 
              message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public Object getResourceId() {
        return resourceId;
    }
    
    // Factory methods para casos comuns
    public static ResourceNotFoundException droneNotFound(Long droneId) {
        return new ResourceNotFoundException("Drone", droneId);
    }
    
    public static ResourceNotFoundException orderNotFound(Long orderId) {
        return new ResourceNotFoundException("Pedido", orderId);
    }
    
    public static ResourceNotFoundException droneNotFound(Long droneId, String reason) {
        return new ResourceNotFoundException(
            String.format("Drone não encontrado: %s", reason),
            "Drone",
            droneId
        );
    }
    
    public static ResourceNotFoundException orderNotFound(Long orderId, String reason) {
        return new ResourceNotFoundException(
            String.format("Pedido não encontrado: %s", reason),
            "Pedido",
            orderId
        );
    }
}




