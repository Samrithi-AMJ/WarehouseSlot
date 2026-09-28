package com.example.WarehouseSlot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_assignments")
public class StockAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item code is required")
    @Column(nullable = false)
    private String itemCode;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String itemName;

    @NotNull(message = "Velocity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Velocity velocity;

    @Positive(message = "Quantity must be greater than zero")
    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    public StockAssignment() {
    }

    public StockAssignment(
            String itemCode,
            String itemName,
            Velocity velocity,
            Integer quantity,
            Bin bin,
            LocalDateTime assignedAt) {

        this.itemCode = itemCode;
        this.itemName = itemName;
        this.velocity = velocity;
        this.quantity = quantity;
        this.bin = bin;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Bin getBin() {
        return bin;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public enum Velocity {
        FAST,
        SLOW
    }
}