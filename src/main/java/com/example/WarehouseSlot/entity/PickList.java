package com.example.WarehouseSlot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_lists")
public class PickList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickListStatus status;

    @OneToMany(
            mappedBy = "pickList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("routeSequence ASC")
    private List<PickListItem> items = new ArrayList<>();

    public PickList() {
    }

    public PickList(
            Order order,
            LocalDateTime generatedAt,
            PickListStatus status) {

        this.order = order;
        this.generatedAt = generatedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public PickListStatus getStatus() {
        return status;
    }

    public List<PickListItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setStatus(PickListStatus status) {
        this.status = status;
    }

    public void setItems(List<PickListItem> items) {
        this.items = items;
    }

    public enum PickListStatus {
        GENERATED,
        IN_PROGRESS,
        COMPLETED
    }
}