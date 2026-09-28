package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.dto.BinRequest;
import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.entity.Zone;
import com.example.WarehouseSlot.exception.BusinessRuleException;
import com.example.WarehouseSlot.exception.ResourceNotFoundException;
import com.example.WarehouseSlot.repository.BinRepo;
import com.example.WarehouseSlot.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinService {

    private final BinRepo binRepository;
    private final ZoneRepository zoneRepository;

    public BinService(
            BinRepo binRepository,
            ZoneRepository zoneRepository) {

        this.binRepository = binRepository;
        this.zoneRepository = zoneRepository;
    }

    public Bin createBin(BinRequest request) {

        if (binRepository.existsByBinCode(request.getBinCode())) {
            throw new BusinessRuleException(
                    "Bin with code '" + request.getBinCode() + "' already exists"
            );
        }

        if (request.getOccupiedQuantity() > request.getCapacity()) {
            throw new BusinessRuleException(
                    "Occupied quantity cannot be greater than bin capacity"
            );
        }

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Zone with id " + request.getZoneId() + " not found"
                        )
                );

        Bin bin = new Bin();

        bin.setBinCode(request.getBinCode());
        bin.setCapacity(request.getCapacity());
        bin.setOccupiedQuantity(request.getOccupiedQuantity());
        bin.setZone(zone);

        return binRepository.save(bin);
    }

    public List<Bin> getAllBins() {
        return binRepository.findAll();
    }

    public Bin getBinById(Long id) {

        return binRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bin with id " + id + " not found"
                        )
                );
    }

    public Bin updateBin(Long id, BinRequest request) {

        Bin existingBin = getBinById(id);

        if (!existingBin.getBinCode().equals(request.getBinCode())
                && binRepository.existsByBinCode(request.getBinCode())) {

            throw new BusinessRuleException(
                    "Bin with code '" + request.getBinCode()
                            + "' already exists"
            );
        }

        if (request.getOccupiedQuantity() > request.getCapacity()) {
            throw new BusinessRuleException(
                    "Occupied quantity cannot be greater than bin capacity"
            );
        }

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Zone with id " + request.getZoneId() + " not found"
                        )
                );

        existingBin.setBinCode(request.getBinCode());
        existingBin.setCapacity(request.getCapacity());
        existingBin.setOccupiedQuantity(request.getOccupiedQuantity());
        existingBin.setZone(zone);

        return binRepository.save(existingBin);
    }

    public void deleteBin(Long id) {

        Bin bin = getBinById(id);

        binRepository.delete(bin);
    }

    public List<Bin> getBinsByZone(String zoneName) {

        return binRepository.findByZoneName(zoneName);
    }

    public int getAvailableCapacity(Long binId) {

        Bin bin = getBinById(binId);

        return bin.getCapacity() - bin.getOccupiedQuantity();
    }
}