package com.example.WarehouseSlot.repository;

import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BinRepo extends JpaRepository<Bin, Long> {

    Optional<Bin> findByBinCode(String binCode);

    boolean existsByBinCode(String binCode);

    List<Bin> findByZone(Zone zone);

    List<Bin> findByZoneName(String zoneName);
}