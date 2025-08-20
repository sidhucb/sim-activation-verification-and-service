package com.cardactivation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/card")
public class CardActivationController {

    private static final Logger log = LoggerFactory.getLogger(CardActivationController.class);

    @Autowired
    private CardActivationService service;
    
    // Upload file with userName parameter
    @PostMapping("/upload")
    public ResponseEntity<CardActivationEntity> uploadAndSaveFile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "userName", required = false) String userName,HttpServletRequest request) {
    	
        if (file == null || file.isEmpty()) {
            System.out.println("❌ File is missing or empty!");
            return ResponseEntity.badRequest().body(null);
        }

        if (userName == null || userName.isBlank()) {
            System.out.println("⚠️ userName not provided!");
        }

        System.out.println("✅ Received file: " + file.getOriginalFilename());
        System.out.println("✅ Content type: " + file.getContentType());
        System.out.println("✅ Size: " + file.getSize() + " bytes");
        System.out.println("✅ userName: " + userName);
        
        log.info("Content-Type: {}", request.getContentType());
        log.info("userName param: {}", userName);
        
        try {
            log.info("Received upload request for user: {}", userName);
            CardActivationEntity saved = service.uploadAndSaveFile(file, userName);
            log.info("File uploaded successfully for user: {}, entityId: {}", userName, saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error while uploading file for user: {}", userName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }

    // Extract text (OCR simulation) by record ID
    @GetMapping("/extract/{id}")
    public ResponseEntity<String> getByExtractedText(@PathVariable Long id) {
        log.info("Request to extract text for recordId: {}", id);
        String text = service.findByExtractedText(id);
        log.info("Extracted text for recordId {}: {}", id, text);
        return ResponseEntity.ok(text);
    }

    // Verify KYC status by record ID
    @PostMapping("/verifyKyc/{id}")
    public ResponseEntity<String> getByVerifyKyc(@PathVariable Long id) {
        log.info("KYC verification requested for recordId: {}", id);
        String status = service.verifyKyc(id);
        log.info("KYC status for recordId {}: {}", id, status);
        return ResponseEntity.ok(status);
    }

    // Get record by userName
    @GetMapping("/get/{userName}")
    public ResponseEntity<CardActivationEntity> getByUserName(@PathVariable String userName) {
        log.info("Fetching record for userName: {}", userName);
        CardActivationEntity entity = service.findByUserName(userName);
        if (entity == null) {
            log.warn("No record found for userName: {}", userName);
            return ResponseEntity.notFound().build();
        }
        log.info("Record found for userName: {}", userName);
        return ResponseEntity.ok(entity);
    }
}
