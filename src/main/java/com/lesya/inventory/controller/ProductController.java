package com.lesya.inventory.controller;

import com.lesya.inventory.dto.product.ProductRequest;
import com.lesya.inventory.dto.product.ProductResponse;
import com.lesya.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/products")
// Provid REST endpoints for product management.
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    // Return all products / searches products via name or SKU.
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        Page<ProductResponse> products;

        if (search == null || search.isBlank()) {
            products = productService.findAllProducts(pageable);
        } else {
            products = productService.searchProducts(
                    search,
                    pageable
            );
        }

        return ResponseEntity.ok(products);
    }


    @GetMapping("/{productCode}")
    // Return Product using its public business identifier.
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable String productCode
    ) {

        return ResponseEntity.ok(
                productService.findByProductCode(productCode)
        );
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Create New Product (ONLY  administrator privilege!!!)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{productCode}")
    @PreAuthorize("hasRole('ADMIN')")
    // Update Existing Product. (ONLY  administrator privilege!!!)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productCode,
            @Valid @RequestBody ProductRequest request
    ) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        productCode,
                        request
                )
        );
    }


    @DeleteMapping("/{productCode}")
    @PreAuthorize("hasRole('ADMIN')")
    // Delete Product. (ONLY  administrator privilege!!!)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productCode
    ) {

        productService.deleteProduct(productCode);

        return ResponseEntity.noContent().build();
    }


}