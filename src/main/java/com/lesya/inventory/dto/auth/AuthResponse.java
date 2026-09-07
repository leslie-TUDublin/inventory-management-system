package com.lesya.inventory.dto.auth;

import com.lesya.inventory.entity.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;


// EN: DTO returned after successful authentication.
@Getter
@AllArgsConstructor
public class AuthResponse {

    // EN: JWT token used to access protected endpoints.
    private String token;

    private String email;

    // EN: Role assigned to the authenticated user.
    private Role role;
}