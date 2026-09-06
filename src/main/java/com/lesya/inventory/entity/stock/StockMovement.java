package com.lesya.inventory.entity.stock;

import com.lesya.inventory.entity.auth.User;
import com.lesya.inventory.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// EN: Records every change made to a product's stock quantity.
@Entity
@Table(
        name = "stock_movements",
        indexes = {
                @Index(name = "idx_stock_movement_product", columnList = "product_id"),
                @Index(name = "idx_stock_movement_date", columnList = "movement_date"),
                @Index(name = "idx_stock_movement_type", columnList = "movement_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(length = 500)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;


    @PrePersist
    public void setMovementDate() {
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
    }
}