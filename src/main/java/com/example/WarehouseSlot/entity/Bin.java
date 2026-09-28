package com.example.WarehouseSlot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "bins")
public class Bin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bin code is required")
    @Column(nullable = false, unique = true)
    private String binCode;

    @Positive(message = "Capacity must be greater than zero")
    @Column(nullable = false)
    private Integer capacity;

    @PositiveOrZero(message = "Occupied quantity cannot be negative")
    @Column(nullable = false)
    private Integer occupiedQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    public Bin() {
    }

    public Bin(String binCode, Integer capacity, Integer occupiedQuantity, Zone zone) {
        this.binCode = binCode;
        this.capacity = capacity;
        this.occupiedQuantity = occupiedQuantity;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public String getBinCode() {
        return binCode;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getOccupiedQuantity() {
        return occupiedQuantity;
    }

    public Zone getZone() {
        return zone;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setOccupiedQuantity(Integer occupiedQuantity) {
        this.occupiedQuantity = occupiedQuantity;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }
}