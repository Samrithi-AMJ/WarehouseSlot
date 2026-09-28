package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.entity.StockAssignment;
import com.example.WarehouseSlot.repository.BinRepo;
import com.example.WarehouseSlot.repository.StockAssignmentRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final BinRepo binRepo;
    private final StockAssignmentRepo stockAssignmentRepo;

    public ReportService(
            BinRepo binRepo,
            StockAssignmentRepo stockAssignmentRepo) {

        this.binRepo = binRepo;
        this.stockAssignmentRepo = stockAssignmentRepo;
    }

    public Map<String, Object> getBinUtilizationReport() {

        List<Bin> bins = binRepo.findAll();

        int totalCapacity = 0;
        int totalOccupied = 0;

        for (Bin bin : bins) {
            totalCapacity += bin.getCapacity();
            totalOccupied += bin.getOccupiedQuantity();
        }

        double utilizationPercentage = 0;

        if (totalCapacity > 0) {
            utilizationPercentage =
                    ((double) totalOccupied / totalCapacity) * 100;
        }

        Map<String, Object> report = new HashMap<>();

        report.put("totalBins", bins.size());
        report.put("totalCapacity", totalCapacity);
        report.put("totalOccupied", totalOccupied);
        report.put("availableCapacity",
                totalCapacity - totalOccupied);
        report.put("utilizationPercentage",
                Math.round(utilizationPercentage * 100.0) / 100.0);

        return report;
    }

    public Map<String, Object> getVelocityReport() {

        List<StockAssignment> assignments =
                stockAssignmentRepo.findAll();

        int fastCount = 0;
        int mediumCount = 0;
        int slowCount = 0;

        int fastQuantity = 0;
        int mediumQuantity = 0;
        int slowQuantity = 0;

        for (StockAssignment assignment : assignments) {

            switch (assignment.getVelocity()) {

                case FAST:
                    fastCount++;
                    fastQuantity += assignment.getQuantity();
                    break;

                case MEDIUM:
                    mediumCount++;
                    mediumQuantity += assignment.getQuantity();
                    break;

                case SLOW:
                    slowCount++;
                    slowQuantity += assignment.getQuantity();
                    break;
            }
        }

        Map<String, Object> report = new HashMap<>();

        report.put("fastItems", fastCount);
        report.put("mediumItems", mediumCount);
        report.put("slowItems", slowCount);

        report.put("fastQuantity", fastQuantity);
        report.put("mediumQuantity", mediumQuantity);
        report.put("slowQuantity", slowQuantity);

        report.put("totalAssignments", assignments.size());

        return report;
    }
}