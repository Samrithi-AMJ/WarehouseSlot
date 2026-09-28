package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.dto.StockAssignmentRequest;
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

    public StockAssignment assignStock(StockAssignmentRequest request) {

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

        selectedBin.setOccupiedQuantity(newOccupiedQuantity);

        binRepo.save(selectedBin);

        StockAssignment assignment = new StockAssignment();

        assignment.setItemCode(request.getItemCode());
        assignment.setQuantity(request.getQuantity());
        assignment.setVelocity(request.getVelocity());
        assignment.setBin(selectedBin);
        assignment.setAssignedAt(LocalDateTime.now());

        return stockAssignmentRepo.save(assignment);
    }

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
         * priority 2 = farther from dispatch
         * priority 3 = more far
         *
         * FAST     -> prefer priority 1
         * MEDIUM   -> prefer priority 2
         * SLOW     -> prefer higher priority numbers
         */

        if (velocity == StockAssignment.ItemVelocity.FAST) {

            return availableBins.stream()
                    .min(Comparator
                            .comparing(
                                    bin -> bin.getZone().getPriority()))
                    .orElse(null);
        }

        if (velocity == StockAssignment.ItemVelocity.MEDIUM) {

            return availableBins.stream()
                    .min(Comparator
                            .comparing(
                                    bin -> Math.abs(
                                            bin.getZone().getPriority() - 2)))
                    .orElse(null);
        }

        return availableBins.stream()
                .max(Comparator
                        .comparing(
                                bin -> bin.getZone().getPriority()))
                .orElse(null);
    }

    public List<StockAssignment> getAllAssignments() {
        return stockAssignmentRepo.findAll();
    }

    public StockAssignment getAssignmentById(Long id) {

        return stockAssignmentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock assignment with id "
                                        + id
                                        + " not found"));
    }

    public List<StockAssignment> getAssignmentsByItemCode(
            String itemCode) {

        return stockAssignmentRepo.findByItemCode(itemCode);
    }

    public List<StockAssignment> getAssignmentsByVelocity(
            StockAssignment.ItemVelocity velocity) {

        return stockAssignmentRepo.findByVelocity(velocity);
    }
}