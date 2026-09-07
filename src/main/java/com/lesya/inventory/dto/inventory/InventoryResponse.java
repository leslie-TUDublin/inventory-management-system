package com.lesya.inventory.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

// EN: DTO returned by the API with Current Inventory Information.

@Getter
@AllArgsConstructor
public class InventoryResponse {

    private String productCode;
    private String productName;
    private Integer quantity;

    // EN: Threshold used to detect low stock.
    private Integer minimumStockLevel;

    // EN: Low Stock - Indicates whether the current quantity is below the threshold.
    private boolean lowStock;
}