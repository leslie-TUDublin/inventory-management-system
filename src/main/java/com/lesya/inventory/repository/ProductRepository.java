package com.lesya.inventory.repository;

import com.lesya.inventory.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // EN: Finds a product by its public code.
    Optional<Product> findByProductCode(String productCode);

    Optional<Product> findBySku(String sku);

    boolean existsByProductCode(String productCode);

    boolean existsBySku(String sku);

    // EN : Searches products by name or SKU using pagination.
    Page<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String name,
            String sku,
            Pageable pageable
    );
}