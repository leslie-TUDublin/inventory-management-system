package com.lesya.inventory.dto.stock;

import com.lesya.inventory.entity.stock.StockMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// EN: DTO used to create a stock movement.
@Getter
@Setter
@NoArgsConstructor
public class StockMovementRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Movement type is required")
    private StockMovementType movementType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}