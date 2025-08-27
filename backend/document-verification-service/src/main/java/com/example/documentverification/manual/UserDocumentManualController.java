package com.example.documentverification.manual;

import com.example.documentverification.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/manual-docs")
@CrossOrigin(origins = "http://localhost:5173")
public class UserDocumentManualController {

    @Autowired
    private UserDocumentManualRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    // Helper to extract token
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    // ---------------- User endpoints ----------------
    @PostMapping("/submit")
    public ResponseEntity<UserDocumentManual> submitManualDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDocumentManual details
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long userId = jwtUtil.extractId(token);
        details.setUserId(userId);
        details.setStatus("pending");
        UserDocumentManual saved = repository.save(details);
        return ResponseEntity.ok(saved);
    }

    // ---------------- Admin endpoints ----------------
    @GetMapping("/pending")
    public ResponseEntity<List<UserDocumentManual>> getPending(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(repository.findByStatus("pending"));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<UserDocumentManual> approve(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<UserDocumentManual> optional = repository.findById(id);
        if (optional.isPresent()) {
            UserDocumentManual doc = optional.get();
            doc.setStatus("approved");
            repository.save(doc);
            return ResponseEntity.ok(doc);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<UserDocumentManual> reject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<UserDocumentManual> optional = repository.findById(id);
        if (optional.isPresent()) {
            UserDocumentManual doc = optional.get();
            doc.setStatus("rejected");
            repository.save(doc);
            return ResponseEntity.ok(doc);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDocumentManual>> getAll(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(repository.findAll());
    }
}
