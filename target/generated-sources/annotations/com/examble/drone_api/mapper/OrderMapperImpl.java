package com.examble.drone_api.mapper;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.dto.OrderResponseDTO;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.model.type.Priority;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-28T18:04:45-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponseDTO toDTO(Order order) {
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

    @Override
    public Order toEntity(OrderCreateRequestDTO order) {
        if ( order == null ) {
            return null;
        }

        Order.OrderBuilder order1 = Order.builder();

        order1.destinationX( order.destinationX() );
        order1.destinationY( order.destinationY() );
        order1.weight( order.weight() );
        order1.priority( order.priority() );

        return order1.build();
    }
}
