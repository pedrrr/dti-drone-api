package com.examble.drone_api.service;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.mapper.OrderMapper;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.repository.interfaces.OrderRepository;
import com.examble.drone_api.service.interfaces.OrderAllocator;
import com.examble.drone_api.service.interfaces.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    OrderAllocator orderAllocator;
    
    public OrderServiceImpl(OrderRepository orderRepository, OrderAllocator orderAllocator) {
        this.orderRepository = orderRepository;
        this.orderAllocator = orderAllocator;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order createOrder(OrderCreateRequestDTO orderCreateRequestDTO) {
        Order newOrder = orderRepository.save(OrderMapper.INSTANCE.toEntity(orderCreateRequestDTO));

        orderAllocator.allocateOrders();
        
        return newOrder;
    }
}
