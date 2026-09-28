package com.example.WarehouseSlot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class BinRequest {

    @NotBlank(message = "Bin code is required")
    private String binCode;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Integer capacity;

    @NotNull(message = "Occupied quantity is required")
    @PositiveOrZero(message = "Occupied quantity cannot be negative")
    private Integer occupiedQuantity;

    @NotNull(message = "Zone ID is required")
    @Positive(message = "Zone ID must be greater than zero")
    private Long zoneId;

    public BinRequest() {
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

    public Long getZoneId() {
        return zoneId;
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

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }
}