package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.dto.BinRequest;
import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.service.BinService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
public class BinController {

    private final BinService binService;

    public BinController(BinService binService) {
        this.binService = binService;
    }

    @PostMapping
    public ResponseEntity<Bin> createBin(
            @Valid @RequestBody BinRequest request) {

        Bin createdBin = binService.createBin(request);

        return new ResponseEntity<>(
                createdBin,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Bin>> getAllBins() {

        return ResponseEntity.ok(
                binService.getAllBins()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bin> getBinById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                binService.getBinById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bin> updateBin(
            @PathVariable Long id,
            @Valid @RequestBody BinRequest request) {

        return ResponseEntity.ok(
                binService.updateBin(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBin(
            @PathVariable Long id) {

        binService.deleteBin(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/zone/{zoneName}")
    public ResponseEntity<List<Bin>> getBinsByZone(
            @PathVariable String zoneName) {

        return ResponseEntity.ok(
                binService.getBinsByZone(zoneName)
        );
    }

    @GetMapping("/{id}/available-capacity")
    public ResponseEntity<Integer> getAvailableCapacity(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                binService.getAvailableCapacity(id)
        );
    }
}