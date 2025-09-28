package com.examble.drone_api.dto;

import com.examble.drone_api.model.type.DroneState;

import java.time.LocalDateTime;
import java.util.List;

public record DroneResponseDTO(Long id,
                               int positionX,
                               int positionY,
                               int weightLimit,
                               int distancePerCargo,
                               double battery,
                               DroneState state,
                               LocalDateTime lastStateChange,
                               LocalDateTime estimatedArrivalTime,
                               List<OrderResponseDTO> orderList) { }
