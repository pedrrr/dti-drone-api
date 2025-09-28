package com.examble.drone_api.mapper;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.dto.OrderResponseDTO;
import com.examble.drone_api.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    OrderResponseDTO toDTO(Order order);
    
    @Mapping(target = "id", ignore = true)
    Order toEntity(OrderCreateRequestDTO order);
}
