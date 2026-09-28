package com.example.WarehouseSlot.controller;

import com.example.WarehouseSlot.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/bin-utilization")
    public ResponseEntity<Map<String, Object>> getBinUtilizationReport() {

        return ResponseEntity.ok(
                reportService.getBinUtilizationReport()
        );
    }

    @GetMapping("/velocity")
    public ResponseEntity<Map<String, Object>> getVelocityReport() {

        return ResponseEntity.ok(
                reportService.getVelocityReport()
        );
    }
}