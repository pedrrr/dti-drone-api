package com.examble.drone_api.repository.interfaces;


import com.examble.drone_api.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    Order save(Order order);
}
