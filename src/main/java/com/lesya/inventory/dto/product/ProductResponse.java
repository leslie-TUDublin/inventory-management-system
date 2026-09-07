package com.lesya.inventory.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class ProductResponse {

    private String productCode;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Long supplierId;
}