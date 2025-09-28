package com.examble.drone_api.mapper;

import com.examble.drone_api.dto.DroneCreateRequestDTO;
import com.examble.drone_api.dto.DroneResponseDTO;
import com.examble.drone_api.dto.OrderResponseDTO;
import com.examble.drone_api.model.Drone;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.DroneState;
import com.examble.drone_api.model.type.Priority;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-28T11:52:26-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class DroneMapperImpl implements DroneMapper {

    @Override
    public DroneResponseDTO toDTO(Drone drone) {
        if ( drone == null ) {
            return null;
        }

        Long id = null;
        int positionX = 0;
        int positionY = 0;
        int weightLimit = 0;
        int distancePerCargo = 0;
        double battery = 0.0d;
        DroneState state = null;
        LocalDateTime lastStateChange = null;
        LocalDateTime estimatedArrivalTime = null;
        List<OrderResponseDTO> orderList = null;

        id = drone.getId();
        positionX = drone.getPositionX();
        positionY = drone.getPositionY();
        weightLimit = drone.getWeightLimit();
        distancePerCargo = drone.getDistancePerCargo();
        battery = drone.getBattery();
        state = drone.getState();
        lastStateChange = drone.getLastStateChange();
        estimatedArrivalTime = drone.getEstimatedArrivalTime();
        orderList = orderListToOrderResponseDTOList( drone.getOrderList() );

        DroneResponseDTO droneResponseDTO = new DroneResponseDTO( id, positionX, positionY, weightLimit, distancePerCargo, battery, state, lastStateChange, estimatedArrivalTime, orderList );

        return droneResponseDTO;
    }

    @Override
    public Drone toEntity(DroneCreateRequestDTO droneCreateRequestDTO) {
        if ( droneCreateRequestDTO == null ) {
            return null;
        }

        Drone.DroneBuilder drone = Drone.builder();

        drone.positionX( droneCreateRequestDTO.positionX() );
        drone.positionY( droneCreateRequestDTO.positionY() );
        drone.weightLimit( droneCreateRequestDTO.weightLimit() );
        drone.distancePerCargo( droneCreateRequestDTO.distancePerCargo() );

        return drone.build();
    }

    protected OrderResponseDTO orderToOrderResponseDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        Long id = null;
        int destinationX = 0;
        int destinationY = 0;
        int weight = 0;
        Priority priority = null;

        id = order.getId();
        destinationX = order.getDestinationX();
        destinationY = order.getDestinationY();
        weight = order.getWeight();
        priority = order.getPriority();

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO( id, destinationX, destinationY, weight, priority );

        return orderResponseDTO;
    }

    protected List<OrderResponseDTO> orderListToOrderResponseDTOList(List<Order> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderResponseDTO> list1 = new ArrayList<OrderResponseDTO>( list.size() );
        for ( Order order : list ) {
            list1.add( orderToOrderResponseDTO( order ) );
        }

        return list1;
    }
}
