package com.lesya.inventory.controller;

import com.lesya.inventory.dto.supplier.SupplierRequest;
import com.lesya.inventory.dto.supplier.SupplierResponse;
import com.lesya.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/suppliers")
// Provide RESTendpoints for supplier management.
public class SupplierController {

    private final SupplierService supplierService;


    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }


    @GetMapping
    // Return all suppliers.
    public ResponseEntity<List<SupplierResponse>> getSuppliers() {

        return ResponseEntity.ok(
                supplierService.findAllSuppliers()
        );
    }


    @GetMapping("/{id}")
    // Return a supplier via its database identifier.
    public ResponseEntity<SupplierResponse> getSupplier(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                supplierService.findById(id)
        );
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Create New supplier. ONLY administrator privilege.
    public ResponseEntity<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierRequest request
    ) {

        SupplierResponse response =
                supplierService.createSupplier(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Updates Existing Supplier. ONLY administrator privilege.
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request
    ) {

        return ResponseEntity.ok(
                supplierService.updateSupplier(
                        id,
                        request
                )
        );
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Delete Supplier. ONLY administrator privilege.
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable Long id
    ) {

        supplierService.deleteSupplier(id);

        return ResponseEntity.noContent().build();
    }
}