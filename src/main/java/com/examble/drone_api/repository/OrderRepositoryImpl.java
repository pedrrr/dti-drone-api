package com.examble.drone_api.repository;

import com.examble.drone_api.model.Order;
import com.examble.drone_api.repository.interfaces.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final List<Order> orderList = new ArrayList<>();
    private Long idCounter = 1L;

    @Override
    public List<Order> findAll() {
        return orderList;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderList.stream()
                .filter(drone->drone.getId().equals(id))
                .findFirst();
    }

    @Override
    public Order save(Order order) {
        order.setId(idCounter++);
        orderList.add(order);
        return order;
    }
}
