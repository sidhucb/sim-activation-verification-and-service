package com.example.documentverification.controller;

import com.example.documentverification.jwt.JwtUtil;
import com.example.documentverification.model.KycDocument;
import com.example.documentverification.service.KycProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    @Autowired
    private KycProcessingService kycService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Upload KYC document (image/pdf) for OCR/AI processing
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadKyc(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("cardType") String cardType
    ) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String userEmail = jwtUtil.extractUsername(token);

        KycDocument doc = kycService.processKyc(file, userEmail, cardType);

        // If OCR/AI failed, frontend can manually fill KYC
        if ("PENDING_MANUAL".equals(doc.getStatus())) {
            return ResponseEntity.status(202).body(Map.of(
                    "message", "OCR failed, please fill KYC details manually",
                    "kycId", doc.getId()
            ));
        }

        return ResponseEntity.ok(doc);
    }

    /**
     * Update manual KYC details after OCR/manual fallback
     */
    @PutMapping("/manual/{id}")
    public ResponseEntity<KycDocument> manualEntry(
            @PathVariable Long id,
            @RequestBody KycDocument manualDetails
    ) {
        KycDocument updated = kycService.updateManualDetails(id, manualDetails);
        return ResponseEntity.ok(updated);
    }
}
