package com.example.documentverification;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtGenerator {

    // Must match JwtUtil SECRET_KEY
    private static final String SECRET_KEY = "ThisIsASecretKeyForJWTGeneration123!";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Generate JWT token
     * @param id User ID
     * @param role USER or ADMIN
     * @param expirationMillis Expiration time in milliseconds from now
     * @return JWT token string
     */
    public static String generateToken(Long id, String role, long expirationMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expirationMillis);

        return Jwts.builder()
                .setSubject("user@example.com")
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static void main(String[] args) {
        // Example: Generate a token for user with id=10, role=USER, expires in 1 hour
        String token = generateToken(7L, "USER", 3600_000); 
        System.out.println("JWT Token: " + token);

        // Example: Generate a token for admin with id=1, role=ADMIN, expires in 1 hour
        String adminToken = generateToken(1L, "ADMIN", 3600_000); 
        System.out.println("Admin JWT Token: " + adminToken);
    }
}
