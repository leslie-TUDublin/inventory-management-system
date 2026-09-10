package com.lesya.inventory.security;

import com.lesya.inventory.entity.auth.User;
import com.lesya.inventory.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// EN: Loads users from the database for Spring Security authentication.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injects the user repository used to load accounts.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // EN: Find the user by email or throw a security exception.
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }
}