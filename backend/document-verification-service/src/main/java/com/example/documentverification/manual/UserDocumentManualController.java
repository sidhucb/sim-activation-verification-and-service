package com.example.documentverification.manual;

import com.example.documentverification.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/manual-docs")
@CrossOrigin(origins = "http://localhost:5173")
public class UserDocumentManualController {

    @Autowired
    private UserDocumentManualRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    // ---------------- User endpoints ----------------

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit")
    public ResponseEntity<UserDocumentManual> submitManualDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDocumentManual details
    ) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = jwtUtil.extractId(token);
        details.setUserId(userId);
        details.setStatus("pending");
        UserDocumentManual saved = repository.save(details);
        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-docs")
    public ResponseEntity<List<UserDocumentManual>> getMyDocs(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = jwtUtil.extractId(token);
        List<UserDocumentManual> list = repository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    // ---------------- Admin endpoints ----------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<UserDocumentManual>> getPending() {
        return ResponseEntity.ok(repository.findByStatus("pending"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<UserDocumentManual> approve(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Optional<UserDocumentManual> optional = repository.findById(id);
        if (optional.isPresent()) {
            UserDocumentManual doc = optional.get();
            doc.setStatus("approved");
            repository.save(doc);

            String token = extractToken(authHeader);
            String email = jwtUtil.extractUsername(token); // Extract email from admin's token (or get from doc.getEmail())

            createSimRequestInSimApp(doc.getUserId(), email, "Approved");

            return ResponseEntity.ok(doc);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<UserDocumentManual> reject(
            @PathVariable Long id
    ) {
        Optional<UserDocumentManual> optional = repository.findById(id);
        if (optional.isPresent()) {
            UserDocumentManual doc = optional.get();
            doc.setStatus("rejected");
            repository.save(doc);
            return ResponseEntity.ok(doc);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDocumentManual>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    // ---------------- Helper methods ----------------

    private void createSimRequestInSimApp(Long userId, String userEmail, String status) {
        String simappUrl = "http://localhost:8086/api/sim/requests";
        Map<String, Object> payload = new HashMap<>();
        payload.put("requestId", "REQ-" + System.currentTimeMillis()); // unique request id
        payload.put("userId", userId);
        payload.put("email", userEmail);
        String username = userEmail.contains("@") ? userEmail.split("@")[0] : userEmail;
        payload.put("username", username);
        payload.put("status", status);

        try {
            restTemplate.postForEntity(simappUrl, payload, String.class);
        } catch (Exception e) {
            System.err.println("Failed to create SIM request for userId " + userId + ": " + e.getMessage());
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }
}
