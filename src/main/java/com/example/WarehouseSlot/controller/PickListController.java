package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.dto.PickListResponse;
import com.example.WarehouseSlot.service.PickListService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picklists")
public class PickListController {

    private final PickListService pickListService;

    public PickListController(
            PickListService pickListService) {

        this.pickListService = pickListService;
    }

    @PostMapping("/{orderId}/generate")
    public ResponseEntity<PickListResponse> generatePickList(
            @PathVariable Long orderId) {

        PickListResponse response =
                pickListService.generatePickList(orderId);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickListResponse> getPickListById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pickListService.getPickListById(id));
    }

    @PostMapping("/{id}/pick")
    public ResponseEntity<PickListResponse> pickStock(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pickListService.pickStock(id));
    }
}