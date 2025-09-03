package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class DlAgentService {

    // Simulate an external DL Agent API call for document verification
    public Map<String, Object> analyzeIdDocument(MultipartFile idDocument) throws IOException {
        System.out.println("DL Agent Service: Sending document to external DL model for analysis...");

        // --- SIMULATED DEEP LEARNING ANALYSIS ---
        // In a real scenario, this would involve:
        // 1. Sending the image data to an actual DL API (e.g., AWS Rekognition, Google Vision, Azure Cognitive Services)
        // 2. The DL API performing OCR, facial recognition, liveness detection, fraud checks.
        // 3. Receiving a structured JSON response.

        // For this demo, we'll simulate a random outcome for realism.
        Random random = new Random();
        boolean isDocumentValid = random.nextBoolean(); // 50/50 chance of being valid
        String extractedName = "John Doe";
        String extractedAadhar = "XXXX-XXXX-1234";

        if (idDocument.getOriginalFilename().toLowerCase().contains("fake")) {
            isDocumentValid = false; // Override for known fake files
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isValid", isDocumentValid);
        result.put("analysis_report", "Simulated DL analysis completed. Identity matching based on provided image and mock database.");

        if (isDocumentValid) {
            result.put("extracted_name", extractedName);
            result.put("extracted_id_number", extractedAadhar);
            result.put("document_type", "Aadhar Card (Simulated)");
        } else {
            result.put("reason", "Document appears to be tampered, or data mismatch detected (simulated).");
        }

        System.out.println("DL Agent Service: Analysis Result: " + (isDocumentValid ? "VALID" : "INVALID"));
        return result;
    }

    // Simulate an external DL Agent API call for face verification (e.g., selfie vs. ID photo)
    public Map<String, Object> verifyFace(String selfieImageBase64, String idPhotoBase64, String expectedName) {
        System.out.println("DL Agent Service: Performing facial verification...");

        Random random = new Random();
        // Simulate varying levels of match based on a random factor
        double matchConfidence = random.nextDouble() * 0.4 + 0.6; // 60-100% confidence for valid, or lower for invalid

        boolean isFaceMatch = matchConfidence > 0.75; // Simulate match if confidence > 75%
        if (random.nextInt(100) < 20) { // Introduce 20% chance of random mismatch for realism
            isFaceMatch = !isFaceMatch;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isMatch", isFaceMatch);
        result.put("confidence", String.format("%.2f%%", matchConfidence * 100));
        result.put("report", "Simulated facial recognition complete.");
        if (!isFaceMatch) {
            result.put("reason", "Face mismatch or liveness detection failed (simulated).");
        }
        System.out.println("DL Agent Service: Facial Verification Result: " + (isFaceMatch ? "MATCH" : "NO MATCH"));
        return result;
    }
}