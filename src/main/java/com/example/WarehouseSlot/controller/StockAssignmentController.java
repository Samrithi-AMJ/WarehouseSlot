package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.dto.StockAssignmentRequest;
import com.example.WarehouseSlot.entity.StockAssignment;
import com.example.WarehouseSlot.service.StockAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-assignments")
public class StockAssignmentController {

    private final StockAssignmentService stockAssignmentService;

    public StockAssignmentController(
            StockAssignmentService stockAssignmentService) {

        this.stockAssignmentService = stockAssignmentService;
    }

    @PostMapping
    public ResponseEntity<StockAssignment> assignStock(
            @Valid @RequestBody StockAssignmentRequest request) {

        StockAssignment assignment =
                stockAssignmentService.assignStock(request);

        return new ResponseEntity<>(
                assignment,
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StockAssignment>> getAllAssignments() {

        return ResponseEntity.ok(
                stockAssignmentService.getAllAssignments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockAssignment> getAssignmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                stockAssignmentService.getAssignmentById(id));
    }

    @GetMapping("/item/{itemCode}")
    public ResponseEntity<List<StockAssignment>> getAssignmentsByItem(
            @PathVariable String itemCode) {

        return ResponseEntity.ok(
                stockAssignmentService
                        .getAssignmentsByItemCode(itemCode));
    }

    @GetMapping("/velocity/{velocity}")
    public ResponseEntity<List<StockAssignment>> getAssignmentsByVelocity(
            @PathVariable StockAssignment.ItemVelocity velocity) {

        return ResponseEntity.ok(
                stockAssignmentService
                        .getAssignmentsByVelocity(velocity));
    }
}