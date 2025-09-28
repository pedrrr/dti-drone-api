package com.examble.drone_api.exception;

/**
 * Exceção base para todas as exceções do sistema de drones.
 * Fornece estrutura comum para tratamento de erros específicos do domínio.
 */
public abstract class DroneSystemException extends RuntimeException {
    
    private final String errorCode;
    private final String userMessage;
    
    protected DroneSystemException(String message) {
        super(message);
        this.errorCode = getClass().getSimpleName();
        this.userMessage = message;
    }
    
    protected DroneSystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = getClass().getSimpleName();
        this.userMessage = message;
    }
    
    protected DroneSystemException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    protected DroneSystemException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s]: %s", getClass().getSimpleName(), errorCode, getMessage());
    }
}



