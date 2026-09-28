package com.example.WarehouseSlot.repository;

import com.example.WarehouseSlot.entity.StockAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAssignmentRepo extends JpaRepository<StockAssignment, Long> {

    List<StockAssignment> findByItemCode(String itemCode);

    List<StockAssignment> findByVelocity(
            StockAssignment.ItemVelocity velocity);
}