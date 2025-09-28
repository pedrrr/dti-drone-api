package com.examble.drone_api.mapper;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.dto.DroneResponseDTO;
import com.examble.drone_api.model.Drone;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DroneMapper {
    DroneMapper INSTANCE = Mappers.getMapper(DroneMapper.class);
    DroneResponseDTO toDTO(Drone drone);
    Drone toEntity(DroneCreateRequestDTO droneCreateRequestDTO);
}
