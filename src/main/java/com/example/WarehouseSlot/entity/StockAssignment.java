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

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Item velocity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemVelocity velocity;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    public StockAssignment() {
    }

    public StockAssignment(
            String itemCode,
            Integer quantity,
            ItemVelocity velocity,
            Bin bin,
            LocalDateTime assignedAt) {

        this.itemCode = itemCode;
        this.quantity = quantity;
        this.velocity = velocity;
        this.bin = bin;
        this.assignedAt = assignedAt;
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

    public ItemVelocity getVelocity() {
        return velocity;
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

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setVelocity(ItemVelocity velocity) {
        this.velocity = velocity;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public enum ItemVelocity {
        FAST,
        MEDIUM,
        SLOW
    }
}