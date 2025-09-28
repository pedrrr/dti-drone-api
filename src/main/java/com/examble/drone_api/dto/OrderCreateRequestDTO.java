package com.examble.drone_api.dto;

import com.examble.drone_api.model.type.Priority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequestDTO(
        @NotNull(message = "A Coordenada X de envio é obrigatória")
        @Min(value = 1, message = "Coordenada X deve ser maior que 0")
        @Max(value = 100, message = "Coordenada X não pode exceder 100")
        int destinationX,
        
        @NotNull(message = "A Coordenada Y de envio é obrigatória")
        @Min(value = 1, message = "Coordenada Y deve ser maior que 0")
        @Max(value = 100, message = "Coordenada Y não pode exceder 100")
        int destinationY,
        
        @NotNull(message = "O peso do pacote a ser enviado é obrigatório")
        @Min(value = 1, message = "Peso deve ser maior que 0")
        @Max(value = 50, message = "Peso não pode exceder 50 kg")
        int weight,
        
        Priority priority
) {
}
