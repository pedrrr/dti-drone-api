package com.examble.drone_api.controller;

import com.examble.drone_api.dto.OrderCreateRequestDTO;
import com.examble.drone_api.dto.OrderResponseDTO;
import com.examble.drone_api.exception.ResourceNotFoundException;
import com.examble.drone_api.mapper.OrderMapper;
import com.examble.drone_api.model.Order;
import com.examble.drone_api.service.interfaces.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/v1/pedidos")
public class OrderController {

    OrderService orderService;
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> postOrder(@RequestBody @Valid
                                                      OrderCreateRequestDTO droneCreateRequestDTO) {
        Order newOrder = orderService.createOrder(droneCreateRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId())
                .toUri();
        return ResponseEntity.created(uri).body(OrderMapper.INSTANCE.toDTO(newOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(id));
        
        return ResponseEntity.ok(OrderMapper.INSTANCE.toDTO(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll().stream()
                .map(OrderMapper.INSTANCE::toDTO)
                .toList());
    }
}
