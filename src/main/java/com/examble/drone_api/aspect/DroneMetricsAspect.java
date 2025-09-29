package com.examble.drone_api.aspect;

import com.examble.drone_api.service.interfaces.DroneMetricsService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class DroneMetricsAspect {
    
    private final DroneMetricsService metricsService;
    private final ConcurrentHashMap<Long, Long> flightStartTimes = new ConcurrentHashMap<>();
    
    public DroneMetricsAspect(DroneMetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @Around("execution(* com.examble.drone_api.service.DroneSimulationServiceImpl.markOrderAsDelivered(..))")
    public Object trackDelivery(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // Executar o método original
            Object result = joinPoint.proceed();
            
            // Extrair o drone do primeiro parâmetro
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof com.examble.drone_api.model.Drone) {
                com.examble.drone_api.model.Drone drone = (com.examble.drone_api.model.Drone) args[0];
                
                // Calcular tempo de entrega desde o início do voo
                long currentTime = System.currentTimeMillis();
                Long flightStartTime = flightStartTimes.get(drone.getId());
                
                if (flightStartTime != null) {
                    long deliveryTime = currentTime - flightStartTime;
                    
                    // Registrar métrica de entrega
                    metricsService.recordDelivery(drone.getId(), deliveryTime);
                    
                    // Remover o tempo de início do voo após a entrega
                    flightStartTimes.remove(drone.getId());
                    
                    log.info("Aspect: Entrega registrada para drone {} em {}ms", drone.getId(), deliveryTime);
                } else {
                    // Fallback: usar tempo estimado baseado na distância
                    long estimatedDeliveryTime = calculateEstimatedDeliveryTime(drone);
                    metricsService.recordDelivery(drone.getId(), estimatedDeliveryTime);
                    log.info("Aspect: Entrega registrada para drone {} com tempo estimado de {}ms", drone.getId(), estimatedDeliveryTime);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Erro ao processar métrica de entrega", e);
            throw e;
        }
    }
    
    @Around("execution(* com.examble.drone_api.service.DroneSimulationServiceImpl.startDroneFlight(..))")
    public Object trackFlightStart(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            // Executar o método original
            Object result = joinPoint.proceed();
            
            // Extrair o drone do primeiro parâmetro
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof com.examble.drone_api.model.Drone) {
                com.examble.drone_api.model.Drone drone = (com.examble.drone_api.model.Drone) args[0];
                
                // Armazenar tempo de início do voo para cálculo posterior
                flightStartTimes.put(drone.getId(), startTime);
                
                log.info("Aspect: Início de voo registrado para drone {} em {}", drone.getId(), startTime);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Erro ao processar métrica de início de voo", e);
            throw e;
        }
    }
    
    @Around("execution(* com.examble.drone_api.service.DroneSimulationServiceImpl.handleReturningToBaseState(..))")
    public Object trackFlightEnd(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // Executar o método original
            Object result = joinPoint.proceed();
            
            // Extrair o drone do primeiro parâmetro
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof com.examble.drone_api.model.Drone) {
                com.examble.drone_api.model.Drone drone = (com.examble.drone_api.model.Drone) args[0];
                
                // Calcular tempo total de voo desde o início
                Long flightStartTime = flightStartTimes.get(drone.getId());
                if (flightStartTime != null) {
                    long totalFlightTime = System.currentTimeMillis() - flightStartTime;
                    
                    // Registrar métrica de tempo de voo
                    metricsService.recordFlightTime(drone.getId(), totalFlightTime);
                    
                    log.info("Aspect: Fim de voo registrado para drone {} com duração total de {}ms", drone.getId(), totalFlightTime);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Erro ao processar métrica de fim de voo", e);
            throw e;
        }
    }
    
    /**
     * Calcula tempo estimado de entrega baseado na distância percorrida
     */
    private long calculateEstimatedDeliveryTime(com.examble.drone_api.model.Drone drone) {
        if (drone.getOrderList() == null || drone.getOrderList().isEmpty()) {
            return 1000; // 1 segundo padrão
        }
        
        // Calcular distância total baseada na posição atual vs base
        double distance = Math.sqrt(
            Math.pow(drone.getPositionX() - 1, 2) + 
            Math.pow(drone.getPositionY() - 1, 2)
        );
        
        // Estimativa: 2 segundos por unidade de distância + 1 segundo base
        return (long) (distance * 2000 + 1000);
    }
}
