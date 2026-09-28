package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.entity.Zone;
import com.example.WarehouseSlot.exception.BusinessRuleException;
import com.example.WarehouseSlot.exception.ResourceNotFoundException;
import com.example.WarehouseSlot.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public Zone createZone(Zone zone) {

        if (zoneRepository.existsByName(zone.getName())) {
            throw new BusinessRuleException(
                    "Zone with name '" + zone.getName() + "' already exists"
            );
        }

        return zoneRepository.save(zone);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {

        return zoneRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Zone with id " + id + " not found"
                        )
                );
    }

    public Zone updateZone(Long id, Zone updatedZone) {

        Zone existingZone = getZoneById(id);

        if (!existingZone.getName().equals(updatedZone.getName())
                && zoneRepository.existsByName(updatedZone.getName())) {

            throw new BusinessRuleException(
                    "Zone with name '" + updatedZone.getName()
                            + "' already exists"
            );
        }

        existingZone.setName(updatedZone.getName());
        existingZone.setPriority(updatedZone.getPriority());

        return zoneRepository.save(existingZone);
    }

    public void deleteZone(Long id) {

        Zone zone = getZoneById(id);

        zoneRepository.delete(zone);
    }
}