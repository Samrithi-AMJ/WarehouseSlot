package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.dto.StockAssignmentRequest;
import com.example.WarehouseSlot.dto.StockAssignmentResponse;
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

    // Assign incoming stock to a suitable bin
    @PostMapping
    public ResponseEntity<StockAssignment> assignStock(
            @Valid @RequestBody StockAssignmentRequest request) {

        StockAssignment assignment =
                stockAssignmentService.assignStock(request);

        return new ResponseEntity<>(
                assignment,
                HttpStatus.CREATED);
    }

    // Get all stock assignments
    @GetMapping
    public ResponseEntity<List<StockAssignmentResponse>> getAllAssignments() {

        List<StockAssignmentResponse> assignments =
                stockAssignmentService.getAllAssignments();

        return ResponseEntity.ok(assignments);
    }

    // Get stock assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<StockAssignment> getAssignmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                stockAssignmentService.getAssignmentById(id));
    }

    // Get stock assignments by item code
    @GetMapping("/item/{itemCode}")
    public ResponseEntity<List<StockAssignment>> getAssignmentsByItem(
            @PathVariable String itemCode) {

        return ResponseEntity.ok(
                stockAssignmentService
                        .getAssignmentsByItemCode(itemCode));
    }

    // Get stock assignments by velocity
    @GetMapping("/velocity/{velocity}")
    public ResponseEntity<List<StockAssignment>> getAssignmentsByVelocity(
            @PathVariable StockAssignment.ItemVelocity velocity) {

        return ResponseEntity.ok(
                stockAssignmentService
                        .getAssignmentsByVelocity(velocity));
    }
}