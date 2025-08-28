package com.example.documentverification;

import com.example.documentverification.DocumentDetails;
import com.example.documentverification.ExtractedCardData;
import com.example.documentverification.DocumentDetailsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DocumentService {

    @Autowired
    private LLMService llmService;

    @Autowired
    private OCRService ocrService;

    @Autowired
    private DocumentDetailsRepository repository;

    @Autowired
    private OpenAIConfig openAIConfig;  // <-- inject config

    private final String CIRCUIT_BREAKER = "documentServiceCB";

    @CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "fallback")
    public DocumentDetails processDocument(DocumentDetails doc, File file) throws Exception {
        doc.setStatus("processing");

        // Get API key from config
        String openApiKey = openAIConfig.getApiKey();

        String ocrText = ocrService.extractText(file);
        ExtractedCardData data = llmService.extractDetailsFromOCR(ocrText, openApiKey);

        if (!validateCardNumber(data.getCardNumber(), doc.getCardType())) {
            doc.setStatus("invalid_" + doc.getCardType().toLowerCase());
            doc.setSimEligibilityMessage("Invalid " + doc.getCardType() + " detected.");
        } else {
            doc.setName(data.getName());
            doc.setDob(data.getDob());
            doc.setGender(data.getGender());
            doc.setAddress(data.getAddress());
            doc.setCardNumber(data.getCardNumber());
            if (data.getDob() != null) {
                try {
                    LocalDate birth = LocalDate.parse(data.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    doc.setAge(Period.between(birth, LocalDate.now()).getYears());
                } catch (DateTimeParseException e) {
                    doc.setAge(null);
                }
            }
            doc.setStatus("verified");
            doc.setSimEligibilityMessage("Document verified successfully.");
        }

        return repository.save(doc);
    }

    private DocumentDetails fallback(DocumentDetails doc, File file, Throwable t) {
        doc.setStatus("ocr_failed");
        doc.setSimEligibilityMessage("Service unavailable. Please submit manually.");
        return repository.save(doc);
    }

    private boolean validateCardNumber(String number, String cardType) {
        if ("PAN".equalsIgnoreCase(cardType)) return number != null && number.matches("[A-Z]{5}[0-9]{4}[A-Z]");
        if ("AADHAR".equalsIgnoreCase(cardType)) return number != null && number.matches("\\d{12}");
        return false;
    }
}
