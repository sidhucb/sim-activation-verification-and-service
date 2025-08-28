package com.example.documentverification.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtGenerator {

    private static final String SECRET_KEY = "ThisIsASecretKeyForJWTGeneration123!";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

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
        String token = generateToken(7L, "USER", 3600_000);
        System.out.println("JWT Token: " + token);
    }
}
