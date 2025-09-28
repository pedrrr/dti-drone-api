package com.examble.drone_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DroneCreateRequestDTO(
        @NotNull(message = "O limite de peso de um drone é obrigatório")
        @Min(value = 1, message = "Limite de peso deve ser maior que 0")
        @Max(value = 50, message = "Limite de peso não pode exceder 50 kg")
        int weightLimit,
        
        @NotNull(message = "A distância de um drone por carga é obrigatória")
        @Min(value = 1, message = "Distância por carga deve ser maior que 0")
        @Max(value = 100, message = "Distância por carga não pode exceder 100 km")
        int distancePerCargo,
        
        @NotNull(message = "A posição X inicial é obrigatória")
        @Min(value = 1, message = "Posição X deve ser maior que 0")
        @Max(value = 100, message = "Posição X não pode exceder 100")
        int positionX,
        
        @NotNull(message = "A posição Y inicial é obrigatória")
        @Min(value = 1, message = "Posição Y deve ser maior que 0")
        @Max(value = 100, message = "Posição Y não pode exceder 100")
        int positionY) {}
