package com.lesya.inventory.entity.product;

import com.lesya.inventory.entity.category.Category;
import com.lesya.inventory.entity.supplier.Supplier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_sku", columnList = "sku")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Product {

    // EN: Internal database identifier.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EN: Public unique identifier used instead of exposing the database ID.
    @Column(nullable = false, unique = true, updatable = false, length = 12)
    private String productCode;

    // EN: Unique SKU (stock keeping unit) used to identify the product.
    // UA: Унікальний артикул для ідентифікації товару.
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @PrePersist
    public void generateProductCode() {
        this.productCode = "PR-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}