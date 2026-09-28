package com.example.WarehouseSlot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "pick_list_items")
public class PickListItem {

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

    @NotNull(message = "Route sequence is required")
    @Positive(message = "Route sequence must be greater than zero")
    @Column(nullable = false)
    private Integer routeSequence;

    @ManyToOne
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    public PickListItem() {
    }

    public PickListItem(
            String itemCode,
            Integer quantity,
            Integer routeSequence,
            PickList pickList,
            Bin bin) {

        this.itemCode = itemCode;
        this.quantity = quantity;
        this.routeSequence = routeSequence;
        this.pickList = pickList;
        this.bin = bin;
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

    public Integer getRouteSequence() {
        return routeSequence;
    }

    public PickList getPickList() {
        return pickList;
    }

    public Bin getBin() {
        return bin;
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

    public void setRouteSequence(Integer routeSequence) {
        this.routeSequence = routeSequence;
    }

    public void setPickList(PickList pickList) {
        this.pickList = pickList;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }
}