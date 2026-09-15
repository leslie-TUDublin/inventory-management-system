package com.lesya.inventory.controller;

import com.lesya.inventory.dto.inventory.InventoryResponse;
import com.lesya.inventory.dto.inventory.InventoryUpdateRequest;
import com.lesya.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/inventory")
// Provide REST endpoints for current inventory information.
public class InventoryController {

    private final InventoryService inventoryService;


    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/product/{productCode}")
    // Return inventory information via the product's public code.
    public ResponseEntity<InventoryResponse> getInventoryByProductCode(
            @PathVariable String productCode
    ) {

        return ResponseEntity.ok(
                inventoryService.findByProductCode(productCode)
        );
    }


    @GetMapping("/product-id/{productId}")
    // EN: Return inventory information via the product database ID.
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                inventoryService.findByProductId(productId)
        );
    }


    @PutMapping("/product/{productId}/settings")
    @PreAuthorize("hasRole('ADMIN')")
    //  Update inventory settings. ONLY  administrator privilege
    public ResponseEntity<InventoryResponse> updateInventorySettings(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryUpdateRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.updateInventorySettings(
                        productId,
                        request
                )
        );
    }
}