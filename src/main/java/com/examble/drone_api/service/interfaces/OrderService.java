package com.examble.drone_api.service.interfaces;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    Order createOrder(OrderCreateRequestDTO droneCreateRequestDTO);
}
