package com.example.WarehouseSlot.dto;

import java.time.LocalDateTime;

public class StockAssignmentResponse {

    private Long id;
    private String itemCode;
    private Integer quantity;
    private String velocity;
    private String binCode;
    private String zoneName;
    private Integer zonePriority;
    private LocalDateTime assignedAt;

    public StockAssignmentResponse() {
    }

    public StockAssignmentResponse(
            Long id,
            String itemCode,
            Integer quantity,
            String velocity,
            String binCode,
            String zoneName,
            Integer zonePriority,
            LocalDateTime assignedAt) {

        this.id = id;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.velocity = velocity;
        this.binCode = binCode;
        this.zoneName = zoneName;
        this.zonePriority = zonePriority;
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

    public String getVelocity() {
        return velocity;
    }

    public String getBinCode() {
        return binCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public Integer getZonePriority() {
        return zonePriority;
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

    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setZonePriority(Integer zonePriority) {
        this.zonePriority = zonePriority;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}