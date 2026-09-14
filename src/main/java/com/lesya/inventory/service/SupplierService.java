package com.lesya.inventory.service;

import com.lesya.inventory.dto.supplier.SupplierRequest;
import com.lesya.inventory.dto.supplier.SupplierResponse;
import com.lesya.inventory.entity.supplier.Supplier;
import com.lesya.inventory.exception.DuplicateResourceException;
import com.lesya.inventory.exception.ResourceNotFoundException;
import com.lesya.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// BUSINESS LOGIC OF PROJECT
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;


    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }


    // Return all suppliers.
    public List<SupplierResponse> findAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // Returns a supplier via its database identifier.
    public SupplierResponse findById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: " + id
                        )
                );

        return mapToResponse(supplier);
    }


    // Create new supplier
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {

        // Check supplier name
        if (supplierRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Supplier with name "
                            + request.getName()
                            + " already exists"
            );
        }

        // Check supplier email
        if (supplierRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Supplier with email "
                            + request.getEmail()
                            + " already exists"
            );
        }

        // Create new Supplier entity
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        // Save supplier
        Supplier savedSupplier = supplierRepository.save(supplier);

        // Return response DTO
        return mapToResponse(savedSupplier);
    }


    // Update supplier information while preserving unique values.
    @Transactional
    public SupplierResponse updateSupplier(
            Long id,
            SupplierRequest request
    ) {

        // Find supplier
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: " + id
                        )
                );

        // Check supplier name
        Supplier supplierWithSameName = supplierRepository
                .findByName(request.getName())
                .orElse(null);

        if (supplierWithSameName != null
                && !supplierWithSameName.getId().equals(supplier.getId())) {

            throw new DuplicateResourceException(
                    "Supplier with name "
                            + request.getName()
                            + " already exists"
            );
        }

        // Check supplier email
        Supplier supplierWithSameEmail = supplierRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (supplierWithSameEmail != null
                && !supplierWithSameEmail.getId().equals(supplier.getId())) {

            throw new DuplicateResourceException(
                    "Supplier with email "
                            + request.getEmail()
                            + " already exists"
            );
        }

        // Update supplier
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        // Save updated supplier
        Supplier updatedSupplier = supplierRepository.save(supplier);

        // Return response DTO
        return mapToResponse(updatedSupplier);
    }


    // Delete supplier ONLY when no products are assigned to it.
    @Transactional
    public void deleteSupplier(Long id) {

        // Find supplier
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: " + id
                        )
                );

        // Check assigned products
        if (!supplier.getProducts().isEmpty()) {

            throw new IllegalStateException(
                    "Supplier cannot be deleted while products are assigned to it"
            );
        }

        // Delete supplier
        supplierRepository.delete(supplier);
    }


    // Map entity to response DTO
    private SupplierResponse mapToResponse(Supplier supplier) {

        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getProducts().size()
        );
    }
}