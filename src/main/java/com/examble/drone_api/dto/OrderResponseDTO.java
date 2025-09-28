package com.examble.drone_api.dto;

import com.examble.drone_api.model.type.Priority;

public record OrderResponseDTO(Long id,
                               int destinationX,
                               int destinationY,
                               int weight,
                               Priority priority,
                               boolean delivered) {
}
