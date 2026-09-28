package com.example.WarehouseSlot.repository;

import com.example.WarehouseSlot.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    boolean existsByName(String name);
}