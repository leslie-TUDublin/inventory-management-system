package com.lesya.inventory.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// EN: DTO used when a user tries to log in.
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    @NotBlank(message = "Password is required")
    private String password;
}