package com.lesya.inventory.security;

import com.lesya.inventory.dto.auth.AuthResponse;
import com.lesya.inventory.dto.auth.LoginRequest;
import com.lesya.inventory.dto.auth.RegisterRequest;
import com.lesya.inventory.entity.auth.Role;
import com.lesya.inventory.entity.auth.User;
import com.lesya.inventory.exception.DuplicateResourceException;
import com.lesya.inventory.exception.InvalidCredentialsException;
import com.lesya.inventory.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // REGISTER USER
    // EN: Creates a new user account and returns a JWT token.
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // EN: Check if email is already taken.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        // EN: Create and populate a new User entity.
        User user = new User();
        user.setEmail(request.getEmail());

        // EN: Encode the password with BCrypt before storing it.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // EN: New registrations always receive the standard USER role.
        user.setRole(Role.USER);

        // EN: Save the user into PostgreSQL.
        User savedUser = userRepository.save(user);

        // EN: Generate a JWT token for the newly registered user.
        String jwtToken = jwtService.generateToken(savedUser.getEmail());

        // EN: Return authentication response with token and user information.
        return new AuthResponse(
                jwtToken,
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // LOGIN USER
    // EN: Authenticates user credentials and returns a JWT token.
    public AuthResponse login(LoginRequest request) {

        try {
            // EN: AuthenticationManager verifies the email and password.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            // EN: Convert Spring Security authentication errors into a custom 401 exception.
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        // EN: Fetch the authenticated user to generate the JWT response.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "User not found after authentication"
                        )
                );

        // EN: Generate a new JWT token.
        String jwtToken = jwtService.generateToken(user.getEmail());

        // EN: Return successful authentication response.
        return new AuthResponse(
                jwtToken,
                user.getEmail(),
                user.getRole()
        );
    }
}