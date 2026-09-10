package com.lesya.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT Authentication Filter checks JWT tokens on incoming requests before protected endpoints are executed.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // EN: Logger for recording security workflow events.
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // EN: Get the Authorization header from the HTTP request.
       String authHeader = request.getHeader("Authorization");

        // EN: Skip JWT processing when the Bearer token is missing.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // EN: Extract the JWT token from the Authorization header.
        String token = authHeader.substring(7);

        try {

            // STEP 1 - ANTI-DOS STEP → Anti-DoS + JWT validation
            //  Validate the token signature and expiration in memory first.
            if (jwtService.validateStructureAndExpiration(token)) {


                // STEP 2 → Extract email
                //  Extract email only after confirming the token is untampered.
                String email = jwtService.extractEmail(token);

                // Check   → Check SecurityContext
                // Continue only when the email exists and authentication is not established yet.
                if (email != null
                        &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    //STEP 3
                    // Load the user from the database after JWT validation succeeds.
                    UserDetails userDetails =
                            customUserDetailsService
                                    .loadUserByUsername(email);

                    // FINAL VALIDATION
                    // EN: Perform a final validation of the token subject and expiration.
                    if (jwtService.isTokenValid(token, email)) {

                        // EN: Create the Spring Security authentication object.
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );


                        // EN: Attach current-request details to the authentication object.
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        // EN: Store authentication in the Spring-Security-context.
                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authToken);

                        log.debug(
                                "Successfully authenticated user: {}",
                                email
                        );
                    }
                }
            }

        } catch (Exception exception) {

            // EN: Prevent unexpected JWT errors from breaking the filter chain.
            log.error(
                    "Unexpected error in JwtAuthenticationFilter: {}",
                    exception.getMessage(),
                    exception
            );
        }

        // EN: Continue processing the remaining security filters.
        filterChain.doFilter(request, response);
    }
}