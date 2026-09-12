package com.lesya.inventory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    // EN: Provides BCrypt password hashing for user credentials.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // EN: Connects UserDetailsService with the password encoder.
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        // EN: Use BCrypt to verify stored password hashes.
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    // EN: Provides the AuthenticationManager used during login.
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    // EN: Configures the Spring Security filter chain.
    // UA: Налаштовує ланцюжок фільтрів Spring Security.
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // EN: Enable CORS support for REST API requests.
                .cors(Customizer.withDefaults())

                // EN: Disable CSRF because the API uses stateless JWT authentication.
                .csrf(csrf -> csrf.disable())

                // EN: Prevent Spring Security from creating HTTP sessions.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // EN: Configure which endpoints are public and protected.
                .authorizeHttpRequests(auth -> auth

                        // EN: Allow unauthenticated access to registration and login.
                        .requestMatchers("/auth/**").permitAll()

                        // EN: Require authentication for all other endpoints.
                        .anyRequest().authenticated()
                )

                // EN: Register our custom authentication provider
                .authenticationProvider(authenticationProvider())

                // EN: Execute JWT authentication before Spring's username/password filter.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}