package com.lesya.inventory.service;

import com.lesya.inventory.dto.product.ProductRequest;
import com.lesya.inventory.dto.product.ProductResponse;
import com.lesya.inventory.entity.category.Category;
import com.lesya.inventory.entity.inventory.Inventory;
import com.lesya.inventory.entity.product.Product;
import com.lesya.inventory.entity.supplier.Supplier;
import com.lesya.inventory.exception.DuplicateResourceException;
import com.lesya.inventory.exception.ResourceNotFoundException;
import com.lesya.inventory.repository.CategoryRepository;
import com.lesya.inventory.repository.InventoryRepository;
import com.lesya.inventory.repository.ProductRepository;
import com.lesya.inventory.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// BUSINESS LOGIC OF PROJECT
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;


    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository,
            InventoryRepository inventoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryRepository = inventoryRepository;
    }



    // Return all products with pagination.
    public Page<ProductResponse> findAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }


    // Search products by name or SKU with pagination.
    public Page<ProductResponse> searchProducts(
            String search,
            Pageable pageable
    ) {

        return productRepository
                .findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
                        search,
                        search,
                        pageable
                )
                .map(this::mapToResponse);
    }



    // Return a product via its public business identifier.
    public ProductResponse findByProductCode(String productCode) {

        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with code: " + productCode
                        )
                );

        return mapToResponse(product);
    }


    // Create new product
    @Transactional //ensures database atomicity.
    public ProductResponse createProduct(ProductRequest request) {

        // Check SKU
        if (productRepository.existsBySku(request.getSku())) {

            throw new DuplicateResourceException(
                    "Product with SKU " + request.getSku() + " already exists"
            );
        }

        // Find category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: "
                                        + request.getCategoryId()
                        )
                );

        // Find supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: "
                                        + request.getSupplierId()
                        )
                );

        // Create new Product entity
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setSupplier(supplier);

        // Save product
        Product savedProduct = productRepository.save(product);

        // Create inventory record
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(0);
        inventory.setMinimumStockLevel(0);

        // Save inventory
        inventoryRepository.save(inventory);

        // Return response DTO
        return mapToResponse(savedProduct);
    }


    // Update product information without changing  public product code.
    @Transactional
    public ProductResponse updateProduct(
            String productCode,
            ProductRequest request
    ) {

        // Find product
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with code: " + productCode
                        )
                );

        // Check SKU
        Product productWithSameSku = productRepository
                .findBySku(request.getSku())
                .orElse(null);

        if (productWithSameSku != null
                && !productWithSameSku.getId().equals(product.getId())) {

            throw new DuplicateResourceException(
                    "Product with SKU " + request.getSku() + " already exists"
            );
        }

        // Find category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: "
                                        + request.getCategoryId()
                        )
                );

        // Find supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: "
                                        + request.getSupplierId()
                        )
                );

        // Update product
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setSupplier(supplier);

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        // Return response DTO
        return mapToResponse(updatedProduct);
    }


    // Delete product via its public business identifier.
    @Transactional
    public void deleteProduct(String productCode) {

        // Find product
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with code: " + productCode
                        )
                );

        // Find inventory
        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElse(null);

        // Delete inventory before deleting the product
        if (inventory != null) {
            inventoryRepository.delete(inventory);
        }

        // Delete product
        productRepository.delete(product);
    }


    // Map entity to response DTO (convert the Product entity into the API response DTO).
    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getProductCode(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getSupplier().getId()
        );
    }
}