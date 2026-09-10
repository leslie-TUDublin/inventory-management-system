package com.lesya.inventory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    // EN: Logger for tracking JWT validation errors.
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    // EN: Secret key used to sign and verify JWT tokens.
    private final String secretKey;
    // EN: JWT token lifetime in milliseconds.
    private final long jwtExpiration;



    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    // CREATE SIGNING KEY - Convert the configured secret into a cryptographic signing key.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    // GENERATE JWT TOKEN - Create a signed JWT containing the user's email.
    public String generateToken(String email) {
        return Jwts.builder()
                // EN: Store the user's email as the JWT subject.
                .subject(email)

                // EN: Store token creation and expiration times.
                .issuedAt(new Date())
                .expiration(
                        new Date(    System.currentTimeMillis() + jwtExpiration      )
                )

                // EN: Sign the JWT using the application secret key.
                .signWith(getSigningKey())

                // EN: Convert the JWT into a compact string.
                .compact();
    }

    // EXTRACT  the user's email from the JWT subject.
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }


    // PRE-VALIDATE WITHOUT DB (ANTI-DOS) - Validate JWT signature and expiration strictly in-memory before calling DB.
    public boolean validateStructureAndExpiration(String token) {

        try {

            // EN: Successful parsing means the signature and expiration are valid.
            getClaims(token);
            return true;

        } catch (ExpiredJwtException exception) {
            log.warn(
                    "JWT token is expired: {}",
                    exception.getMessage()
            );

        } catch (SignatureException exception) {
            log.error(
                    "Invalid JWT signature: {}",
                    exception.getMessage()
            );

        } catch (MalformedJwtException exception) {
            log.error(
                    "Invalid JWT token structure: {}",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            log.error(
                    "JWT token validation failed: {}",
                    exception.getMessage()
            );
        }

        return false;
    }

    // VALIDATE TOKEN WITH EMAIL - Check that the JWT belongs to the expected user and is not expired.
    public boolean isTokenValid(String token, String email) {

        try {
            // EN: Extract the email stored inside the JWT.
            // UA: Витягуємо email, збережений усередині JWT.
            String extractedEmail = extractEmail(token);

            return extractedEmail.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception exception) {
            return false;
        }
    }

    // CHECK TOKEN EXPIRATION - Check whether the JWT expiration time has already passed.
    private boolean isTokenExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Клеймсики
    // GET CLAIMS -  Parse the signed JWT and return its claims.
    private Claims getClaims(String token) {

        return Jwts.parser()

                // EN: Verify the JWT signature using the application key.
                .verifyWith(getSigningKey())

                // EN: Build the JWT parser.
                .build()

                // EN: Parse a signed JWT containing claims.
                .parseSignedClaims(token)

                // EN: Return the claims from the JWT payload.
                .getPayload();
    }
}