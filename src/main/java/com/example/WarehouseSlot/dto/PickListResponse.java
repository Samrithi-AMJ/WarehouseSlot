package com.example.WarehouseSlot.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PickListResponse {

    private Long pickListId;
    private String orderNumber;
    private LocalDateTime generatedAt;
    private String status;
    private List<PickItemResponse> items;

    public PickListResponse() {
    }

    public PickListResponse(
            Long pickListId,
            String orderNumber,
            LocalDateTime generatedAt,
            String status,
            List<PickItemResponse> items) {

        this.pickListId = pickListId;
        this.orderNumber = orderNumber;
        this.generatedAt = generatedAt;
        this.status = status;
        this.items = items;
    }

    public Long getPickListId() {
        return pickListId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getStatus() {
        return status;
    }

    public List<PickItemResponse> getItems() {
        return items;
    }

    public void setPickListId(Long pickListId) {
        this.pickListId = pickListId;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(List<PickItemResponse> items) {
        this.items = items;
    }

    public static class PickItemResponse {

        private String itemCode;
        private Integer quantity;
        private Integer routeSequence;
        private String binCode;
        private String zoneName;
        private Integer zonePriority;

        public PickItemResponse() {
        }

        public PickItemResponse(
                String itemCode,
                Integer quantity,
                Integer routeSequence,
                String binCode,
                String zoneName,
                Integer zonePriority) {

            this.itemCode = itemCode;
            this.quantity = quantity;
            this.routeSequence = routeSequence;
            this.binCode = binCode;
            this.zoneName = zoneName;
            this.zonePriority = zonePriority;
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

        public String getBinCode() {
            return binCode;
        }

        public String getZoneName() {
            return zoneName;
        }

        public Integer getZonePriority() {
            return zonePriority;
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

        public void setBinCode(String binCode) {
            this.binCode = binCode;
        }

        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public void setZonePriority(Integer zonePriority) {
            this.zonePriority = zonePriority;
        }
    }
}