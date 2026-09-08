package com.lesya.inventory.repository;

import com.lesya.inventory.entity.stock.StockMovement;
import com.lesya.inventory.entity.stock.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // EN: Returns stock movements for a specific product.
    Page<StockMovement> findByProductId(
            Long productId,
            Pageable pageable
    );

    // EN: Returns movements filtered by their type.
    Page<StockMovement> findByMovementType(
            StockMovementType movementType,
            Pageable pageable
    );

    // EN: Returns movements for a product filtered by movement type.
    Page<StockMovement> findByProductIdAndMovementType(
            Long productId,
            StockMovementType movementType,
            Pageable pageable
    );


    // EN: Returns stock movements created by a specific user.
    List<StockMovement> findByPerformedById(Long userId);
}