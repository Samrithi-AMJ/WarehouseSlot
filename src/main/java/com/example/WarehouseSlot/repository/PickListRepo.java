package com.example.WarehouseSlot.repository;

import com.example.WarehouseSlot.entity.Order;
import com.example.WarehouseSlot.entity.PickList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickListRepo extends JpaRepository<PickList, Long> {

    Optional<PickList> findByOrder(Order order);

    boolean existsByOrder(Order order);
}