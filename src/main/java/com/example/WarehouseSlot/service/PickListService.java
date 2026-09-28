package com.example.WarehouseSlot.service;

import com.example.WarehouseSlot.dto.PickListResponse;
import com.example.WarehouseSlot.entity.Bin;
import com.example.WarehouseSlot.entity.Order;
import com.example.WarehouseSlot.entity.OrderItem;
import com.example.WarehouseSlot.entity.PickList;
import com.example.WarehouseSlot.entity.PickListItem;
import com.example.WarehouseSlot.entity.StockAssignment;
import com.example.WarehouseSlot.exception.BusinessRuleException;
import com.example.WarehouseSlot.exception.ResourceNotFoundException;
import com.example.WarehouseSlot.repository.BinRepo;
import com.example.WarehouseSlot.repository.OrderRepo;
import com.example.WarehouseSlot.repository.PickListRepo;
import com.example.WarehouseSlot.repository.StockAssignmentRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PickListService {

    private final PickListRepo pickListRepo;
    private final OrderRepo orderRepo;
    private final StockAssignmentRepo stockAssignmentRepo;
    private final BinRepo binRepo;

    public PickListService(
            PickListRepo pickListRepo,
            OrderRepo orderRepo,
            StockAssignmentRepo stockAssignmentRepo,
            BinRepo binRepo) {

        this.pickListRepo = pickListRepo;
        this.orderRepo = orderRepo;
        this.stockAssignmentRepo = stockAssignmentRepo;
        this.binRepo = binRepo;
    }

    @Transactional
    public PickListResponse generatePickList(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order with id " + orderId + " not found"));

        if (pickListRepo.existsByOrder(order)) {
            throw new BusinessRuleException(
                    "Pick list already exists for order "
                            + order.getOrderNumber());
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessRuleException(
                    "Cannot generate pick list for an empty order");
        }

        PickList pickList = new PickList();

        pickList.setOrder(order);
        pickList.setGeneratedAt(LocalDateTime.now());
        pickList.setStatus(PickList.PickListStatus.GENERATED);

        List<PickListItem> pickListItems = new ArrayList<>();

        for (OrderItem orderItem : order.getItems()) {

            StockAssignment assignment =
                    findStockAssignment(orderItem.getItemCode());

            Bin bin = assignment.getBin();

            if (bin.getOccupiedQuantity() < orderItem.getQuantity()) {

                throw new BusinessRuleException(
                        "Insufficient stock for item "
                                + orderItem.getItemCode()
                                + ". Available: "
                                + bin.getOccupiedQuantity()
                                + ", Required: "
                                + orderItem.getQuantity());
            }

            PickListItem pickListItem = new PickListItem();

            pickListItem.setItemCode(orderItem.getItemCode());
            pickListItem.setQuantity(orderItem.getQuantity());
            pickListItem.setPickList(pickList);
            pickListItem.setBin(bin);

            pickListItems.add(pickListItem);
        }

        /*
         * Sort bins by zone priority.
         *
         * Priority 1 = nearest dispatch
         * Priority 2 = middle
         * Priority 3 = far
         */

        pickListItems.sort(
                Comparator.comparing(
                        item -> item.getBin()
                                .getZone()
                                .getPriority()
                )
        );

        int routeSequence = 1;

        for (PickListItem item : pickListItems) {
            item.setRouteSequence(routeSequence++);
        }

        pickList.setItems(pickListItems);

        PickList savedPickList =
                pickListRepo.save(pickList);

        return convertToResponse(savedPickList);
    }

    private StockAssignment findStockAssignment(
            String itemCode) {

        List<StockAssignment> assignments =
                stockAssignmentRepo.findByItemCode(itemCode);

        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No stock assignment found for item "
                            + itemCode);
        }

        /*
         * Select the assignment whose bin currently
         * has the highest available stock.
         */
        return assignments.stream()
                .filter(assignment ->
                        assignment.getBin()
                                .getOccupiedQuantity() > 0)
                .max(
                        Comparator.comparing(
                                assignment ->
                                        assignment.getBin()
                                                .getOccupiedQuantity()
                        )
                )
                .orElseThrow(() ->
                        new BusinessRuleException(
                                "No available stock for item "
                                        + itemCode));
    }

    public PickListResponse getPickListById(Long id) {

        PickList pickList = pickListRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pick list with id "
                                        + id
                                        + " not found"));

        return convertToResponse(pickList);
    }

    @Transactional
    public PickListResponse pickStock(Long id) {

        PickList pickList = pickListRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pick list with id "
                                        + id
                                        + " not found"));

        if (pickList.getStatus()
                == PickList.PickListStatus.COMPLETED) {

            throw new BusinessRuleException(
                    "Pick list is already completed");
        }

        pickList.setStatus(
                PickList.PickListStatus.IN_PROGRESS);

        for (PickListItem item : pickList.getItems()) {

            Bin bin = item.getBin();

            if (bin.getOccupiedQuantity()
                    < item.getQuantity()) {

                throw new BusinessRuleException(
                        "Insufficient stock in bin "
                                + bin.getBinCode()
                                + " for item "
                                + item.getItemCode());
            }

            int remainingStock =
                    bin.getOccupiedQuantity()
                            - item.getQuantity();

            bin.setOccupiedQuantity(remainingStock);

            binRepo.save(bin);
        }

        pickList.setStatus(
                PickList.PickListStatus.COMPLETED);

        PickList savedPickList =
                pickListRepo.save(pickList);

        return convertToResponse(savedPickList);
    }

    private PickListResponse convertToResponse(
            PickList pickList) {

        List<PickListResponse.PickItemResponse> items =
                pickList.getItems()
                        .stream()
                        .map(item ->
                                new PickListResponse.PickItemResponse(
                                        item.getItemCode(),
                                        item.getQuantity(),
                                        item.getRouteSequence(),
                                        item.getBin().getBinCode(),
                                        item.getBin().getZone().getName(),
                                        item.getBin().getZone().getPriority()
                                )
                        )
                        .toList();

        return new PickListResponse(
                pickList.getId(),
                pickList.getOrder().getOrderNumber(),
                pickList.getGeneratedAt(),
                pickList.getStatus().name(),
                items
        );
    }
}