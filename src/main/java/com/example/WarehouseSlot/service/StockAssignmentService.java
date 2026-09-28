package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.dto.StockAssignmentRequest;
import com.example.WarehouseSlot.dto.StockAssignmentResponse;
import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.entity.StockAssignment;
import com.example.WarehouseSlot.exception.BusinessRuleException;
import com.example.WarehouseSlot.exception.ResourceNotFoundException;
import com.example.WarehouseSlot.repository.BinRepo;
import com.example.WarehouseSlot.repository.StockAssignmentRepo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class StockAssignmentService {

    private final StockAssignmentRepo stockAssignmentRepo;
    private final BinRepo binRepo;

    public StockAssignmentService(
            StockAssignmentRepo stockAssignmentRepo,
            BinRepo binRepo) {

        this.stockAssignmentRepo = stockAssignmentRepo;
        this.binRepo = binRepo;
    }

    // Assign stock to a suitable bin
    public StockAssignment assignStock(
            StockAssignmentRequest request) {

        List<Bin> bins = binRepo.findAll();

        if (bins.isEmpty()) {
            throw new BusinessRuleException(
                    "No bins are available for stock assignment");
        }

        Bin selectedBin = findSuitableBin(
                bins,
                request.getVelocity(),
                request.getQuantity());

        if (selectedBin == null) {
            throw new BusinessRuleException(
                    "No suitable bin with enough capacity is available for "
                            + request.getVelocity()
                            + " velocity item");
        }

        int newOccupiedQuantity =
                selectedBin.getOccupiedQuantity()
                        + request.getQuantity();

        if (newOccupiedQuantity > selectedBin.getCapacity()) {
            throw new BusinessRuleException(
                    "Stock quantity exceeds available bin capacity");
        }

        // Update bin occupancy
        selectedBin.setOccupiedQuantity(newOccupiedQuantity);

        binRepo.save(selectedBin);

        // Create stock assignment
        StockAssignment assignment =
                new StockAssignment();

        assignment.setItemCode(request.getItemCode());
        assignment.setQuantity(request.getQuantity());
        assignment.setVelocity(request.getVelocity());
        assignment.setBin(selectedBin);
        assignment.setAssignedAt(LocalDateTime.now());

        // Save stock assignment
        return stockAssignmentRepo.save(assignment);
    }

    // Find the best bin based on velocity
    private Bin findSuitableBin(
            List<Bin> bins,
            StockAssignment.ItemVelocity velocity,
            Integer quantity) {

        List<Bin> availableBins = bins.stream()
                .filter(bin ->
                        bin.getCapacity()
                                - bin.getOccupiedQuantity()
                                >= quantity)
                .toList();

        if (availableBins.isEmpty()) {
            return null;
        }

        /*
         * Zone priority:
         *
         * priority 1 = closest to dispatch
         * priority 2 = medium distance
         * priority 3 = far from dispatch
         *
         * FAST   -> prefer priority 1
         * MEDIUM -> prefer priority 2
         * SLOW   -> prefer highest priority number
         */

        if (velocity == StockAssignment.ItemVelocity.FAST) {

            return availableBins.stream()
                    .min(Comparator.comparing(
                            bin -> bin.getZone().getPriority()))
                    .orElse(null);
        }

        if (velocity == StockAssignment.ItemVelocity.MEDIUM) {

            return availableBins.stream()
                    .min(Comparator.comparing(
                            bin -> Math.abs(
                                    bin.getZone().getPriority() - 2)))
                    .orElse(null);
        }

        return availableBins.stream()
                .max(Comparator.comparing(
                        bin -> bin.getZone().getPriority()))
                .orElse(null);
    }

    // Get all stock assignments
    public List<StockAssignmentResponse> getAllAssignments() {

        return stockAssignmentRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get stock assignment by ID
    public StockAssignment getAssignmentById(Long id) {

        return stockAssignmentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock assignment with id "
                                        + id
                                        + " not found"));
    }

    // Get assignments by item code
    public List<StockAssignment> getAssignmentsByItemCode(
            String itemCode) {

        return stockAssignmentRepo.findByItemCode(itemCode);
    }

    // Get assignments by velocity
    public List<StockAssignment> getAssignmentsByVelocity(
            StockAssignment.ItemVelocity velocity) {

        return stockAssignmentRepo.findByVelocity(velocity);
    }

    // Convert entity to response DTO
    private StockAssignmentResponse convertToResponse(
            StockAssignment assignment) {

        Bin bin = assignment.getBin();

        return new StockAssignmentResponse(
                assignment.getId(),
                assignment.getItemCode(),
                assignment.getQuantity(),
                assignment.getVelocity().name(),
                bin.getBinCode(),
                bin.getZone().getName(),
                bin.getZone().getPriority(),
                assignment.getAssignedAt()
        );
    }
}