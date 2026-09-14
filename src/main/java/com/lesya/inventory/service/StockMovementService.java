package com.lesya.inventory.service;

import com.lesya.inventory.dto.stock.StockMovementRequest;
import com.lesya.inventory.dto.stock.StockMovementResponse;
import com.lesya.inventory.entity.auth.User;
import com.lesya.inventory.entity.inventory.Inventory;
import com.lesya.inventory.entity.product.Product;
import com.lesya.inventory.entity.stock.StockMovement;
import com.lesya.inventory.entity.stock.StockMovementType;
import com.lesya.inventory.exception.ResourceNotFoundException;
import com.lesya.inventory.repository.InventoryRepository;
import com.lesya.inventory.repository.ProductRepository;
import com.lesya.inventory.repository.StockMovementRepository;
import com.lesya.inventory.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//  M A I N     BUSINESS LOGIC OF PROJECT
@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;


    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }


    // Return stock movement history with pagination.
    public Page<StockMovementResponse> findAllMovements(
            Pageable pageable
    ) {

        return stockMovementRepository.findAll(pageable)
                .map(this::mapToResponse);
    }


    // Return stock movement history for a specific product.
    public Page<StockMovementResponse> findByProduct(
            Long productId,
            Pageable pageable
    ) {

        return stockMovementRepository
                .findByProductId(productId, pageable)
                .map(this::mapToResponse);
    }


    // Return stock movements filtered by movement type.
    public Page<StockMovementResponse> findByMovementType(
            StockMovementType movementType,
            Pageable pageable
    ) {

        return stockMovementRepository
                .findByMovementType(movementType, pageable)
                .map(this::mapToResponse);
    }


    // Create stock movement and update the current inventory quantity.
    @Transactional
    public StockMovementResponse createMovement(
            StockMovementRequest request,
            String userEmail
    ) {

        // Find product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: "
                                        + request.getProductId()
                        )
                );

        // Find inventory
        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product ID: "
                                        + product.getId()
                        )
                );

        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + userEmail
                        )
                );

        // Calculate new inventory quantity
        int currentQuantity = inventory.getQuantity();
        int movementQuantity = request.getQuantity();

        switch (request.getMovementType()) {

            // Incoming stock
            case IN -> inventory.setQuantity(
                    currentQuantity + movementQuantity
            );

            // Outgoing stock
            case OUT -> {

                if (currentQuantity < movementQuantity) {

                    throw new IllegalStateException(
                            "Insufficient stock for product: "
                                    + product.getProductCode()
                    );
                }

                inventory.setQuantity(
                        currentQuantity - movementQuantity
                );
            }

            // Stock adjustment
            case ADJUSTMENT -> inventory.setQuantity(
                    movementQuantity
            );
        }

        // Save updated inventory
        inventoryRepository.save(inventory);

        // Create new StockMovement entity
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(request.getMovementType());
        movement.setQuantity(movementQuantity);
        movement.setReason(request.getReason());
        movement.setPerformedBy(user);

        // Save movement
        StockMovement savedMovement =
                stockMovementRepository.save(movement);

        // Return response DTO
        return mapToResponse(savedMovement);
    }


    // Map entity to response DTO
    private StockMovementResponse mapToResponse(
            StockMovement movement
    ) {

        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getProductCode(),
                movement.getProduct().getName(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getMovementDate(),
                movement.getReason(),
                movement.getPerformedBy().getEmail()
        );
    }
}