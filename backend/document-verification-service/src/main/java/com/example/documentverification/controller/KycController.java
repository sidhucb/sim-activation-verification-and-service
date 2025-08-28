package com.example.documentverification.controller;

import com.example.documentverification.jwt.JwtUtil;
import com.example.documentverification.model.KycDocument;
import com.example.documentverification.service.KycService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    private final KycService kycService;
    private final JwtUtil jwtUtil;

    public KycController(KycService kycService, JwtUtil jwtUtil) {
        this.kycService = kycService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Upload endpoint — accepts optional manual fields.
     * If OCR/AI fails, fallback will use manual fields (if present) or create a manual-entry placeholder row.
     *
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "manualName", required = false) String manualName,
            @RequestParam(value = "manualDob", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate manualDob,
            @RequestParam(value = "manualCardType", required = false) String manualCardType,
            @RequestParam(value = "manualCardNumber", required = false) String manualCardNumber
    ) throws Exception {

        String token = authHeader.replaceFirst("Bearer ", "");
        String userEmail = jwtUtil.extractUsername(token);

        KycDocument saved = kycService.process(file, userEmail, manualName, manualDob, manualCardType, manualCardNumber);

        if (saved.isManualEntry() && (saved.getName() == null || saved.getDob() == null)) {
            return ResponseEntity.accepted().body(
                "OCR/AI failed after retries. A manual-entry record was created with id=" + saved.getId() +
                ". Please call /api/kyc/manual/" + saved.getId() + " to submit manual details."
            );
        }

        return ResponseEntity.ok(saved);
    }
}
