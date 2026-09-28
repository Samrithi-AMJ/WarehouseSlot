package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.entity.Zone;
import com.example.WarehouseSlot.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public ResponseEntity<Zone> createZone(
            @Valid @RequestBody Zone zone) {

        Zone createdZone = zoneService.createZone(zone);

        return new ResponseEntity<>(
                createdZone,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {

        return ResponseEntity.ok(
                zoneService.getAllZones()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zone> getZoneById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                zoneService.getZoneById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Zone> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody Zone zone) {

        return ResponseEntity.ok(
                zoneService.updateZone(id, zone)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(
            @PathVariable Long id) {

        zoneService.deleteZone(id);

        return ResponseEntity.noContent().build();
    }
}