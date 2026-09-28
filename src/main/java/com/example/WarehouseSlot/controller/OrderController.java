package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.dto.OrderRequest;
import com.example.WarehouseSlot.entity.Order;
import com.example.WarehouseSlot.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest request) {

        Order createdOrder =
                orderService.createOrder(request);

        return new ResponseEntity<>(
                createdOrder,
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(
                orderService.getOrderByNumber(orderNumber));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status));
    }
}