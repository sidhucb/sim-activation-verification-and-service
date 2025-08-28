package simapp;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "ThisIsASecretKeyForJWTGeneration123!";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public Claims extractClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).getSubject(); // JWT 'sub' is email
    }

    public Long extractId(String token) {
        return ((Number) extractClaims(token).get("id")).longValue();
    }

    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

}
