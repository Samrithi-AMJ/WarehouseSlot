package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.dto.OrderRequest;
import com.example.WarehouseSlot.entity.Order;
import com.example.WarehouseSlot.entity.OrderItem;
import com.example.WarehouseSlot.exception.BusinessRuleException;
import com.example.WarehouseSlot.exception.ResourceNotFoundException;
import com.example.WarehouseSlot.repository.OrderRepo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepo orderRepo;

    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order createOrder(OrderRequest request) {

        if (orderRepo.existsByOrderNumber(request.getOrderNumber())) {
            throw new BusinessRuleException(
                    "Order with number '"
                            + request.getOrderNumber()
                            + "' already exists");
        }

        Order order = new Order();

        order.setOrderNumber(request.getOrderNumber());

        if (request.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        } else {
            order.setStatus(request.getStatus());
        }

        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {

            OrderItem item = new OrderItem();

            item.setItemCode(itemRequest.getItemCode());
            item.setQuantity(itemRequest.getQuantity());
            item.setOrder(order);

            orderItems.add(item);
        }

        order.setItems(orderItems);

        return orderRepo.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Long id) {

        return orderRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order with id "
                                        + id
                                        + " not found"));
    }

    public Order getOrderByNumber(String orderNumber) {

        return orderRepo.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order with number '"
                                        + orderNumber
                                        + "' not found"));
    }

    public Order updateOrderStatus(
            Long id,
            Order.OrderStatus status) {

        Order order = getOrderById(id);

        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Completed order cannot be updated");
        }

        order.setStatus(status);

        return orderRepo.save(order);
    }
}