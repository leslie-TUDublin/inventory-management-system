package com.lesya.inventory.controller;

import com.lesya.inventory.dto.stock.StockMovementRequest;
import com.lesya.inventory.dto.stock.StockMovementResponse;
import com.lesya.inventory.entity.stock.StockMovementType;
import com.lesya.inventory.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/stock-movements")
// Provide RESTendpoints for stock movement history and inventory changes.
public class StockMovementController {

    private final StockMovementService stockMovementService;


    public StockMovementController(
            StockMovementService stockMovementService
    ) {
        this.stockMovementService = stockMovementService;
    }


    @GetMapping
    // Return all stock movements with pagination.
    public ResponseEntity<Page<StockMovementResponse>> getMovements(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                stockMovementService.findAllMovements(pageable)
        );
    }


    @GetMapping("/product/{productId}")
    // Return stock movement history for a specific product.
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByProduct(
            @PathVariable Long productId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                stockMovementService.findByProduct(
                        productId,
                        pageable
                )
        );
    }


    @GetMapping("/type/{movementType}")
    // Return stock movements filtered by movement type.
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByType(
            @PathVariable StockMovementType movementType,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                stockMovementService.findByMovementType(
                        movementType,
                        pageable
                )
        );
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Creates a stock movement and updates inventory quantity. ONLY administrator privilege
    public ResponseEntity<StockMovementResponse> createMovement(
            @Valid @RequestBody StockMovementRequest request,
            Authentication authentication
    ) {

        StockMovementResponse response =
                stockMovementService.createMovement(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}