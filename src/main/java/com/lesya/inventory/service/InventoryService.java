package com.lesya.inventory.service;

import com.lesya.inventory.dto.inventory.InventoryResponse;
import com.lesya.inventory.dto.inventory.InventoryUpdateRequest;
import com.lesya.inventory.entity.inventory.Inventory;
import com.lesya.inventory.exception.ResourceNotFoundException;
import com.lesya.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// BUSINESS LOGIC OF PROJECT
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;


    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }




    // Get inventory by product code (return current inventory information using the product's public business identifier.
    public InventoryResponse findByProductCode(String productCode) {

        // Find inventory
        Inventory inventory = inventoryRepository
                .findByProductProductCode(productCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product code: "
                                        + productCode
                        )
                );

        return mapToResponse(inventory);
    }



    // Return current inventory information via the product ID.
    public InventoryResponse findByProductId(Long productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product ID: "
                                        + productId
                        )
                );

        return mapToResponse(inventory);
    }


    // Update the minimum stock threshold without changing current quantity.
    @Transactional
    public InventoryResponse updateInventorySettings(
            Long productId,
            InventoryUpdateRequest request
    ) {

        // Find inventory
        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product ID: "
                                        + productId
                        )
                );

        // Update minimum stock level
        inventory.setMinimumStockLevel(
                request.getMinimumStockLevel()
        );

        // Save inventory
        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        // Return response DTO
        return mapToResponse(updatedInventory);
    }


    // Map entity to response DTO
    private InventoryResponse mapToResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getProduct().getProductCode(),
                inventory.getProduct().getName(),
                inventory.getQuantity(),
                inventory.getMinimumStockLevel(),
                inventory.getQuantity()
                        <= inventory.getMinimumStockLevel()
        );
    }
}