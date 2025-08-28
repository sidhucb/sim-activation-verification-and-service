package com.example.documentverification.service;

import com.example.documentverification.model.*;
import com.example.documentverification.repository.KycDocumentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class KycService {

    private final KycDocumentRepository kycRepository;
    private final OcrService ocrService;
    private final AiParsingService aiParsingService;

    public KycService(KycDocumentRepository kycRepository, OcrService ocrService, AiParsingService aiParsingService) {
        this.kycRepository = kycRepository;
        this.ocrService = ocrService;
        this.aiParsingService = aiParsingService;
    }

    /**
     * Process uploaded KYC image with resilience.
     */
    @Retry(name = "ocrRetry", fallbackMethod = "ocrFallback")
    @CircuitBreaker(name = "ocrService", fallbackMethod = "ocrFallback")
    public KycDocument process(MultipartFile file) throws IOException {
        // 1) Save uploaded file
        String savedPath = saveFile(file);

        // 2) Perform OCR extraction
        String rawText = ocrService.extractText(savedPath);
        if (rawText == null || rawText.isBlank()) {
            throw new RuntimeException("OCR returned empty text");
        }

        // 3) AI Parsing
        KycData parsed = aiParsingService.parseTextWithAi(rawText);

        // 4) If AI parsing partially fails → still store with OCR text
        if (parsed == null) {
            parsed = new KycData();
            parsed.setName("OCR_ONLY");
            parsed.setCardType("UNKNOWN");
            parsed.setCardNumber("UNKNOWN");
        }

        // 5) Validate only if data exists
        validateIfAvailable(parsed);

        // 6) Save extracted data
        KycDocument kyc = new KycDocument();
        kyc.setName(parsed.getName());
        kyc.setDob(parsed.getDob());
        kyc.setCardType(parsed.getCardType());
        kyc.setCardNumber(maskCardNumber(parsed.getCardNumber()));
        kyc.setCreatedAt(LocalDateTime.now());
        kyc.setUpdatedAt(LocalDateTime.now());
        return kycRepository.save(kyc);
    }

    /**
     * Fallback: called only if OCR+AI completely fail after retries
     */
    public KycDocument ocrFallback(MultipartFile file, Throwable throwable) {
        System.err.println("OCR/AI failed after retries. Reason: " + throwable.getMessage());

        KycDocument manualKyc = new KycDocument();
        manualKyc.setName("PENDING_MANUAL");
        manualKyc.setCardType("PENDING");
        manualKyc.setCardNumber("PENDING");
        manualKyc.setCreatedAt(LocalDateTime.now());
        manualKyc.setUpdatedAt(LocalDateTime.now());
        manualKyc = kycRepository.save(manualKyc);

        System.out.println("Manual-entry record created with id=" + manualKyc.getId()
                + ". Please call /api/kyc/manual/" + manualKyc.getId() + " to submit manual details.");
        return manualKyc;
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String filePath = uploadDir + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }

    private void validateIfAvailable(KycData data) {
        if (data.getCardNumber() != null && data.getCardNumber().length() < 4) {
            throw new RuntimeException("Invalid card number");
        }
        if (data.getName() != null && data.getName().isBlank()) {
            throw new RuntimeException("Name is missing");
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null) return "UNKNOWN";
        if (cardNumber.length() <= 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public Optional<KycDocument> findById(Long id) {
        return kycRepository.findById(id);
    }
}
