package com.example.WarehouseSlot.dto;

import com.example.WarehouseSlot.entity.Order.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequest {

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    private OrderStatus status;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public static class OrderItemRequest {

        @NotBlank(message = "Item code is required")
        private String itemCode;

        @jakarta.validation.constraints.NotNull(message = "Quantity is required")
        @jakarta.validation.constraints.Positive(message = "Quantity must be greater than zero")
        private Integer quantity;

        public OrderItemRequest() {
        }

        public String getItemCode() {
            return itemCode;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}