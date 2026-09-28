package com.example.WarehouseSlot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item code is required")
    @Column(nullable = false)
    private String itemCode;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Column(nullable = false)
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderItem() {
    }

    public OrderItem(String itemCode, Integer quantity, Order order) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}