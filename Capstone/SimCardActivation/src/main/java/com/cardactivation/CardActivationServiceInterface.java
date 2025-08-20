package com.cardactivation;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Primary;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Primary
@Service
public class CardActivationServiceInterface implements CardActivationService {

    private static final String UPLOAD_DIR = "upload/";

    private final CardActivationRepository repository;
    private final OpenAiChatModel openAiChatModel;
    private final OcrService ocrService;

    // ✅ Constructor-based injection
    public CardActivationServiceInterface(CardActivationRepository repository,
                                          OpenAiChatModel openAiChatModel,
                                          OcrService ocrService) {
        this.repository = repository;
        this.openAiChatModel = openAiChatModel;
        this.ocrService = ocrService;
    }

    @Override
    public CardActivationEntity uploadAndSaveFile(MultipartFile file, String userName) throws IOException {
        // 1. Ensure upload directory exists
        Path uploadDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 2. Save file locally
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 3. Create entity
        CardActivationEntity entity = new CardActivationEntity(
                userName,
                file.getOriginalFilename(),
                filePath.toString(),
                file.getBytes()
        );

        // 4. OCR extraction
        String extractedText;
        try {
            extractedText = ocrService.extractText(file);
        } catch (Exception e) {
            extractedText = "OCR extraction failed: " + e.getMessage();
        }
        entity.setExtractedText(extractedText);

        // 5. Validate / Enrich extracted text with AI
        try {
            Prompt prompt = new Prompt("Validate or improve this extracted text for KYC: " + extractedText);
            ChatResponse response = openAiChatModel.call(prompt);
            String validatedText = response.getResult().getOutput().getText();
            entity.setAiValidation(validatedText);
        } catch (Exception e) {
            entity.setAiValidation("AI validation failed: " + e.getMessage());
        }

        // 6. Default statuses
        entity.setKycStatus("PENDING");
        entity.setSimStatus("REQUESTED");

        // 7. Save to DB
        return repository.save(entity);
    }

    @Override
    @CircuitBreaker(name = "ocrService", fallbackMethod = "ocrFallback")
    public String findByExtractedText(Long id) {
        Optional<CardActivationEntity> opt = repository.findById(id);
        return opt.map(CardActivationEntity::getExtractedText)
                  .orElse("Record not found, try a different file.");
    }

    // ✅ CircuitBreaker fallback
    public String ocrFallback(Long id, Throwable t) {
        return "OCR service is unavailable. Please try again later.";
    }

    @Override
    public String verifyKyc(Long id) {
        Optional<CardActivationEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return "Record not found";
        }

        CardActivationEntity entity = opt.get();

        // ✅ Improved validation: prefer AI over raw OCR
        if (entity.getAiValidation() != null && entity.getAiValidation().toLowerCase().contains("valid")) {
            entity.setKycStatus("Completed");
        } else if (entity.getExtractedText() != null && !entity.getExtractedText().isEmpty()) {
            entity.setKycStatus("Pending Review"); // fallback
        } else {
            entity.setKycStatus("Rejected");
        }

        repository.save(entity);
        return "KYC Status: " + entity.getKycStatus();
    }

    @Override
    public CardActivationEntity findByUserName(String userName) {
        return repository.findByUserName(userName).orElse(null);
    }
}
