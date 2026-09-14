package com.lesya.inventory.repository;

import com.lesya.inventory.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// EN: Provides database operations for current inventory quantities.
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find the inventory record via product ID.
    Optional<Inventory> findByProductId(Long productId);

    // Find  the inventory record via the product's public business identifier.
    Optional<Inventory> findByProductProductCode(String productCode);

    // Check whether an inventory record already exists for a product.
    boolean existsByProductId(Long productId);
}