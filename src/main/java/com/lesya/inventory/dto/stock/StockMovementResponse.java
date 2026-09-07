package com.lesya.inventory.dto.stock;

import com.lesya.inventory.entity.stock.StockMovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private String productCode;
    private String productName;
    private StockMovementType movementType;
    private Integer quantity;
    private LocalDateTime movementDate;
    private String reason;

    // EN: Email of the user who created the movement.
    private String performedBy;
}