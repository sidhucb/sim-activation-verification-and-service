package com.example.documentverification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.TesseractException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import com.example.documentverification.JwtUtil; // Correct import path

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}) 
public class DocumentController {

    @Autowired
    private OcrService ocrService;

    @Autowired
    private DocumentRepository documentRepository;
    
    // IMPORTANT: Inject the JwtUtil to validate the token
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
        @RequestHeader("Authorization") String authorizationHeader, // Get the Authorization header
        @RequestParam("cardType") String cardType,
        @RequestParam("image1") MultipartFile image1,
        @RequestParam(value = "image2", required = false) MultipartFile image2
    ) {
        // Step 1: Validate the JWT and get the user ID
        String jwtToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String userId;
        try {
            // Ensure the JWT is valid and get the user ID from its payload
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token.");
            }
            userId = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            // If any part of validation fails, return unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token: " + e.getMessage());
        }

        // Step 2: Proceed with the rest of the logic, using the extracted userId
        if (image1.isEmpty() || ("Aadhar".equalsIgnoreCase(cardType) && (image2 == null || image2.isEmpty()))) {
            return ResponseEntity.badRequest().body("Please upload all required images.");
        }

        try {
            // Pass the userId to the service layer to associate it with the document
            DocumentDetails savedDetails = ocrService.processDocument(cardType, image1, image2, userId);
            return ResponseEntity.ok(savedDetails);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing document: " + e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            String errorMessage = (cause != null) ? cause.getMessage() : e.getMessage();
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during LLM processing: " + errorMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("LLM processing was interrupted: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
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
}
