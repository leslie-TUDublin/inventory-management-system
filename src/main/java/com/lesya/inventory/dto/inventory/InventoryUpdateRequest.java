package com.lesya.inventory.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryUpdateRequest {

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level must not be negative")
    private Integer minimumStockLevel;
}