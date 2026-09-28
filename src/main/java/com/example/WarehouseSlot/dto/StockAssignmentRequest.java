package com.example.WarehouseSlot.dto;

import com.example.WarehouseSlot.entity.StockAssignment.ItemVelocity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class StockAssignmentRequest {

    @NotBlank(message = "Item code is required")
    private String itemCode;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Item velocity is required")
    private ItemVelocity velocity;

    public StockAssignmentRequest() {
    }

    public String getItemCode() {
        return itemCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ItemVelocity getVelocity() {
        return velocity;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setVelocity(ItemVelocity velocity) {
        this.velocity = velocity;
    }
}