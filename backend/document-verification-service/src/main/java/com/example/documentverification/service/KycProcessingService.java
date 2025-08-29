package com.example.documentverification.service;

import com.example.documentverification.model.KycData;
import com.example.documentverification.model.KycDocument;
import com.example.documentverification.repository.KycDocumentRepository;
import com.example.documentverification.util.MaskingUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class KycProcessingService {

    private final KycDocumentRepository repository;
    private final OcrService ocrService;

    public KycProcessingService(KycDocumentRepository repository, OcrService ocrService) {
        this.repository = repository;
        this.ocrService = ocrService;
    }

    @Retry(name = "ocrRetry")
    @CircuitBreaker(name = "ocrCircuitBreaker", fallbackMethod = "manualFallback")
    public KycDocument processKyc(MultipartFile file, String userEmail, String frontendCardType) throws IOException {
        // 1️⃣ Extract KYC data via OCR/AI
        KycData data = ocrService.extract(file);

        // 2️⃣ Use frontend card type if OCR fails to detect
        if (data.getCardType() == null || "UNKNOWN".equalsIgnoreCase(data.getCardType())) {
            data.setCardType(frontendCardType);
        }

        // 3️⃣ Validate card type and age
        validateCardType(data.getCardType());
        if (data.getDob() != null && data.getDob().isAfter(java.time.LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("User must be 18+");
        }

        // 4️⃣ Mask card number
        String masked = "PAN".equalsIgnoreCase(data.getCardType())
                ? MaskingUtil.maskPan(data.getCardNumber())
                : MaskingUtil.maskAadhar(data.getCardNumber());

        // 5️⃣ Create and save KycDocument
        KycDocument doc = new KycDocument();
        doc.setUserId(userEmail);
        doc.setName(data.getName());
        doc.setDob(data.getDob());
        doc.setCardType(data.getCardType());
        doc.setCardNumber(masked);
        doc.setStatus("APPROVED");
        doc.setRemarks("Auto-extracted");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());

        return repository.save(doc);
    }

    // Fallback if OCR/AI fails
    public KycDocument manualFallback(MultipartFile file, String userEmail, String frontendCardType, Throwable ex) {
        KycDocument doc = new KycDocument();
        doc.setUserId(userEmail);
        doc.setStatus("PENDING_MANUAL");
        doc.setRemarks("OCR/AI failed: " + (ex != null ? ex.getMessage() : "unknown"));
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        return repository.save(doc);
    }

    // Update manually entered KYC
    public KycDocument updateManualDetails(Long id, KycDocument manualDetails) {
        validateCardType(manualDetails.getCardType());
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(manualDetails.getName());
                    existing.setDob(manualDetails.getDob());
                    existing.setCardType(manualDetails.getCardType());
                    existing.setCardNumber(
                            "PAN".equalsIgnoreCase(manualDetails.getCardType())
                            ? MaskingUtil.maskPan(manualDetails.getCardNumber())
                            : MaskingUtil.maskAadhar(manualDetails.getCardNumber())
                    );
                    existing.setStatus("KYC_COMPLETED");
                    existing.setRemarks("Updated manually by admin");
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("KYC record not found for id: " + id));
    }

    private void validateCardType(String cardType) {
        if (cardType == null || (!cardType.equalsIgnoreCase("AADHAAR") && !cardType.equalsIgnoreCase("PAN"))) {
            throw new IllegalArgumentException("Invalid card type. Must be AADHAAR or PAN.");
        }
    }
}
