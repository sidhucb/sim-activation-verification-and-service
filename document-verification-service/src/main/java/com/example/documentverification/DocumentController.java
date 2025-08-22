package com.example.documentverification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "jfif", "png", "tif", "tiff", "bmp", "pdf"
    );

    private final OcrService ocrService;
    private final DocumentRepository documentRepository;

    public DocumentController(OcrService ocrService, DocumentRepository documentRepository) {
        this.ocrService = ocrService;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam String cardType,
            @RequestParam MultipartFile image1,
            @RequestParam(required = false) MultipartFile image2) {

        try {
            if (image1.isEmpty() || ("aadhaar".equalsIgnoreCase(cardType) && (image2 == null || image2.isEmpty()))) {
                return ResponseEntity.badRequest().body("Upload required image(s)");
            }

            if (!isSupportedFormat(image1)) {
                log.warn("Unsupported file format for image1");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unsupported file format for image1. Supported: JPG, JPEG, JFIF, PNG, TIFF, BMP, PDF.");
            }
            if (image2 != null && !image2.isEmpty() && !isSupportedFormat(image2)) {
                log.warn("Unsupported file format for image2");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unsupported file format for image2. Supported: JPG, JPEG, JFIF, PNG, TIFF, BMP, PDF.");
            }

            DocumentDetails details = ocrService.processDocument(cardType, image1, image2);
            maskSensitiveData(cardType, details);
            documentRepository.save(details);

            return ResponseEntity.ok(details);

        } catch (IOException | TesseractException e) {
            log.error("OCR failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred while processing the document.");
        }
    }

    private boolean isSupportedFormat(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                String extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
                if (ALLOWED_EXTENSIONS.contains(extension)) {
                    return true;
                }
            }
        }
        // Fallback to Tika MIME check
        try {
            return ocrService.isSupportedMimeType(file);
        } catch (IOException e) {
            log.warn("Failed to detect MIME type with Tika: {}", e.getMessage());
            return false;
        }
    }

    private void maskSensitiveData(String cardType, DocumentDetails details) {
        if (details.getCardNumber() == null || details.getCardNumber().length() < 4) {
            return;
        }

        String num = details.getCardNumber();
        if ("aadhaar".equalsIgnoreCase(cardType)) {
            details.setCardNumber("XXXX-XXXX-" + num.substring(num.length() - 4));
        } else if ("pan".equalsIgnoreCase(cardType) || "pancard".equalsIgnoreCase(cardType)) {
            details.setCardNumber("XXXXX" + num.substring(num.length() - 4));
        } else {
            details.setCardNumber("****" + num.substring(num.length() - 4));
        }
    }
}