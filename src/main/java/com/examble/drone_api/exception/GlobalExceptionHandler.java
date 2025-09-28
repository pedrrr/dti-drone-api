package com.examble.drone_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento de exceções de validação de entrada (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("errorCode", "INPUT_VALIDATION_ERROR");
        response.put("message", "Dados inválidos fornecidos");
        response.put("details", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    // Tratamento de exceções do sistema de drones
    @ExceptionHandler(DroneSystemException.class)
    public ResponseEntity<Map<String, Object>> handleDroneSystemException(DroneSystemException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", getHttpStatusForDroneSystemException(ex).value());
        response.put("error", ex.getErrorCode());
        response.put("message", ex.getUserMessage());
        response.put("technicalMessage", ex.getMessage());
        
        // Adicionar informações específicas baseadas no tipo de exceção
        addSpecificExceptionDetails(response, ex);
        
        return ResponseEntity.status(getHttpStatusForDroneSystemException(ex)).body(response);
    }

    // Tratamento de exceções de validação de drones
    @ExceptionHandler(DroneValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDroneValidationException(DroneValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "DRONE_VALIDATION_ERROR");
        response.put("message", ex.getUserMessage());
        response.put("technicalMessage", ex.getMessage());
        
        if (ex.getDroneId() != null) {
            response.put("droneId", ex.getDroneId());
        }
        if (ex.getValidationField() != null) {
            response.put("validationField", ex.getValidationField());
        }
        
        return ResponseEntity.badRequest().body(response);
    }

    // Tratamento de exceções de alocação de pedidos
    @ExceptionHandler(OrderAllocationException.class)
    public ResponseEntity<Map<String, Object>> handleOrderAllocationException(OrderAllocationException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "ORDER_ALLOCATION_ERROR");
        response.put("message", ex.getUserMessage());
        response.put("technicalMessage", ex.getMessage());
        
        if (ex.getOrderId() != null) {
            response.put("orderId", ex.getOrderId());
        }
        if (ex.getDroneId() != null) {
            response.put("droneId", ex.getDroneId());
        }
        if (ex.getAllocationReason() != null) {
            response.put("allocationReason", ex.getAllocationReason());
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Tratamento de exceções de simulação de voos
    @ExceptionHandler(FlightSimulationException.class)
    public ResponseEntity<Map<String, Object>> handleFlightSimulationException(FlightSimulationException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "FLIGHT_SIMULATION_ERROR");
        response.put("message", ex.getUserMessage());
        response.put("technicalMessage", ex.getMessage());
        
        if (ex.getDroneId() != null) {
            response.put("droneId", ex.getDroneId());
        }
        if (ex.getSimulationPhase() != null) {
            response.put("simulationPhase", ex.getSimulationPhase());
        }
        if (ex.getFlightOperation() != null) {
            response.put("flightOperation", ex.getFlightOperation());
        }
        
        return ResponseEntity.internalServerError().body(response);
    }

    // Tratamento de exceções de recursos não encontrados
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "RESOURCE_NOT_FOUND");
        response.put("message", ex.getUserMessage());
        response.put("technicalMessage", ex.getMessage());
        
        if (ex.getResourceType() != null) {
            response.put("resourceType", ex.getResourceType());
        }
        if (ex.getResourceId() != null) {
            response.put("resourceId", ex.getResourceId());
        }
        
        return ResponseEntity.notFound().build();
    }

    // Tratamento de exceções legadas (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "INVALID_ARGUMENT");
        response.put("message", ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    // Tratamento de exceções genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "INTERNAL_SERVER_ERROR");
        response.put("message", "Ocorreu um erro interno no servidor");
        response.put("technicalMessage", ex.getMessage());
        
        return ResponseEntity.internalServerError().body(response);
    }

    // Métodos auxiliares
    private HttpStatus getHttpStatusForDroneSystemException(DroneSystemException ex) {
        if (ex instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof DroneValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof OrderAllocationException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof FlightSimulationException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void addSpecificExceptionDetails(Map<String, Object> response, DroneSystemException ex) {
        if (ex instanceof DroneValidationException) {
            DroneValidationException dve = (DroneValidationException) ex;
            if (dve.getDroneId() != null) {
                response.put("droneId", dve.getDroneId());
            }
            if (dve.getValidationField() != null) {
                response.put("validationField", dve.getValidationField());
            }
        } else if (ex instanceof OrderAllocationException) {
            OrderAllocationException oae = (OrderAllocationException) ex;
            if (oae.getOrderId() != null) {
                response.put("orderId", oae.getOrderId());
            }
            if (oae.getDroneId() != null) {
                response.put("droneId", oae.getDroneId());
            }
            if (oae.getAllocationReason() != null) {
                response.put("allocationReason", oae.getAllocationReason());
            }
        } else if (ex instanceof FlightSimulationException) {
            FlightSimulationException fse = (FlightSimulationException) ex;
            if (fse.getDroneId() != null) {
                response.put("droneId", fse.getDroneId());
            }
            if (fse.getSimulationPhase() != null) {
                response.put("simulationPhase", fse.getSimulationPhase());
            }
            if (fse.getFlightOperation() != null) {
                response.put("flightOperation", fse.getFlightOperation());
            }
        } else if (ex instanceof ResourceNotFoundException) {
            ResourceNotFoundException rnfe = (ResourceNotFoundException) ex;
            if (rnfe.getResourceType() != null) {
                response.put("resourceType", rnfe.getResourceType());
            }
            if (rnfe.getResourceId() != null) {
                response.put("resourceId", rnfe.getResourceId());
            }
        }
    }
}
