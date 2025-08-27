package com.example.documentverification;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException; // Import JwtException
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // IMPORTANT: This key MUST BE THE SAME as the one used in the user-service
    // The key is hardcoded here, which is fine for development.
    private final String SECRET_KEY = "ThisIsASecretKeyForJWTGeneration123!";
    private final Key signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class); // Assumes 'role' is stored in JWT claims
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public Long extractId(String token) {
        Claims claims = extractAllClaims(token);
        // "id" claim is stored as Integer, cast to Number then get longValue
        Number idNumber = claims.get("id", Number.class);
        return idNumber.longValue();
    }


    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token) {
        try {
            // Validate the token by parsing it
            Claims claims = extractAllClaims(token);
            // Additionally check if the token has expired
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            // Log the exception for debugging purposes
            System.err.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
}
