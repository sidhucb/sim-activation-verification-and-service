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
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:3000") // Allows frontend to connect

public class DocumentController {

    // THIS LINE IS CRUCIAL
    @Autowired
    private OcrService ocrService; 

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    // THIS METHOD MUST CALL THE OCR SERVICE
    public ResponseEntity<?> uploadDocument(
        @RequestParam("cardType") String cardType,
        @RequestParam("image1") MultipartFile image1,
        @RequestParam(value = "image2", required = false) MultipartFile image2
    ) {
        if (image1.isEmpty() || ("Aadhar".equalsIgnoreCase(cardType) && (image2 == null || image2.isEmpty()))) {
            return ResponseEntity.badRequest().body("Please upload all required images.");
        }

        try {
            // It should be calling this line:
            DocumentDetails savedDetails = ocrService.processDocument(cardType, image1, image2);
            return ResponseEntity.ok(savedDetails); // And returning the result
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing document: " + e.getMessage());
        }
    }

    // (The other methods for getPendingDocuments and updateStatus remain the same)
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


