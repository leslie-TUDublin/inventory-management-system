package com.lesya.inventory.repository;

import com.lesya.inventory.entity.auth.Role;
import com.lesya.inventory.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// User Repository
// EN: Provides database operations for user accounts.
public interface UserRepository extends JpaRepository<User, Long> {

    // Find By Email
    Optional<User> findByEmail(String email);

    // EN: Checks whether the email is already registered.
    boolean existsByEmail(String email);

    // EN: Checks whether at least one user has the specified role.
    boolean existsByRole(Role role);
}