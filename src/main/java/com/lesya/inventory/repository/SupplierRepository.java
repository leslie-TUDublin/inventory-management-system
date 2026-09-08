package com.lesya.inventory.repository;

import com.lesya.inventory.entity.supplier.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SupplierRepository extends JpaRepository<Supplier, Long> {


    Optional<Supplier> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Supplier> findByName(String name);

    boolean existsByName(String name);
}