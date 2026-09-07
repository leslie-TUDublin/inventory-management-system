package com.lesya.inventory.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class SupplierResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    private int productCount;
}