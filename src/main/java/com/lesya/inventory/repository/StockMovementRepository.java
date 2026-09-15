package com.lesya.inventory.repository;

import com.lesya.inventory.entity.stock.StockMovement;
import com.lesya.inventory.entity.stock.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Stock Movement Repository provides database operations for stock movement history.
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // return stock movements for a specific product.
    Page<StockMovement> findByProductId(
            Long productId,
            Pageable pageable
    );


    // Return movements filtered via their type.
    Page<StockMovement> findByMovementType(
            StockMovementType movementType,
            Pageable pageable
    );

    // Return movements for a product filtered via movement type.
    Page<StockMovement> findByProductIdAndMovementType(
            Long productId,
            StockMovementType movementType,
            Pageable pageable
    );


    // Return stock movements created by a specific user.
    List<StockMovement> findByPerformedById(Long userId);


    // Check whether a product already has stock movement history.
    boolean existsByProductId(Long productId);
}