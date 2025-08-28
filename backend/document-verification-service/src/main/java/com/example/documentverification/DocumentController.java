package com.example.documentverification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.TesseractException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}) 
public class DocumentController {

    @Autowired
    private OcrService ocrService;

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private RestTemplate restTemplate;


    // ---------------- User Endpoints ----------------

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
        @RequestParam("cardType") String cardType,
        @RequestParam("image1") MultipartFile image1,
        @RequestParam(value = "image2", required = false) MultipartFile image2
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // logged-in user ID/email

        if (image1.isEmpty() || ("Aadhar".equalsIgnoreCase(cardType) && (image2 == null || image2.isEmpty()))) {
            return ResponseEntity.badRequest().body("Please upload all required images.");
        }

        try {
            DocumentDetails savedDetails = ocrService.processDocument(cardType, image1, image2, userId);
            return ResponseEntity.ok(savedDetails);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing document: " + e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            String errorMessage = (cause != null) ? cause.getMessage() : e.getMessage();
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during LLM processing: " + errorMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return ResponseEntity.status(500).body("LLM processing was interrupted: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public List<DocumentDetails> getPendingDocuments() {
        return documentRepository.findByStatus("pending");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DocumentDetails> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        Optional<DocumentDetails> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isPresent()) {
            DocumentDetails document = optionalDocument.get();
            if ("approved".equalsIgnoreCase(status) || "disapproved".equalsIgnoreCase(status)) {
                document.setStatus(status);
                documentRepository.save(document);
                return ResponseEntity.ok(document);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/eligibility")
    public List<Object[]> getEligibilityStatus() {
        return documentRepository.findEligibilityStatus();
    }

    // ---------------- Admin Endpoints ----------------
    // Only users with ROLE_ADMIN can access these

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<DocumentDetails>> getAllDocuments() {
        List<DocumentDetails> documents = documentRepository.findAll();
        return ResponseEntity.ok(documents);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<DocumentDetails> approveDocument(@PathVariable Long id) {
        // Fetch the document
        DocumentDetails doc = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));

        // Update status
        doc.setStatus("Approved");
        documentRepository.save(doc);

        // Extract uploader email from document (userId field)
        String uploaderEmail = doc.getUserId(); // "shonnappan@gmail.com" for example

        // Create a SIM request in SimApp for the correct user with status "Approved"
        createSimRequestInSimApp(uploaderEmail, "Approved");

        return ResponseEntity.ok(doc);
    }

    
    private void createSimRequestInSimApp(String userEmail, String status) {
        String simappUrl = "http://localhost:8086/api/sim/requests";
        Map<String, Object> payload = new HashMap<>();

        payload.put("requestId", "REQ-" + System.currentTimeMillis());
        payload.put("email", userEmail);
        String username = userEmail.contains("@") ? userEmail.split("@")[0] : userEmail;
        payload.put("username", username);

        // Set status based on document approval
        payload.put("status", status);

        try {
            restTemplate.postForEntity(simappUrl, payload, String.class);
        } catch (Exception e) {
            System.err.println("Failed to create SIM request for " + userEmail + ": " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject/{id}")
    public ResponseEntity<DocumentDetails> rejectDocument(@PathVariable Long id) {
        DocumentDetails doc = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus("Rejected");
        documentRepository.save(doc);
        return ResponseEntity.ok(doc);
    }

}
