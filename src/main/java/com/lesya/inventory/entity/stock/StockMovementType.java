package com.lesya.inventory.entity.stock;


public enum StockMovementType {

    // Incoming stock increases the available stock quantity.
    IN,

    // Outgoing stock decreases the available stock quantity.
    OUT,

    // Stock adjustment corrects stock quantity after an inventory check.
    ADJUSTMENT
}