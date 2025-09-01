package com.example.documentverification;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class OcrService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LlmService llmService;

    @CircuitBreaker(name = "ocrService", fallbackMethod = "ocrFallback")
    public DocumentDetails processDocument(String cardType, MultipartFile image1, MultipartFile image2, Long userId)
            throws IOException, TesseractException, ExecutionException, InterruptedException {

        Tesseract tesseract = new Tesseract();
        String tessDataPath = System.getProperty("user.dir") + File.separator + "tessdata";
        File tessDataFolder = new File(tessDataPath);

        if (!tessDataFolder.exists() || !tessDataFolder.isDirectory()) {
            throw new IOException("Tessdata folder not found at: " + tessDataPath);
        }
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");

        StringBuilder extractedText = new StringBuilder();
        extractedText.append(tesseract.doOCR(toBufferedImage(image1)));
        if (image2 != null && !image2.isEmpty()) {
            extractedText.append("\n").append(tesseract.doOCR(toBufferedImage(image2)));
        }

        ExtractedAadharData extractedData = llmService.extractDataWithLlm(extractedText.toString()).get();

        DocumentDetails details = new DocumentDetails();
        details.setCardType(cardType);
        details.setPreparedAt(LocalDateTime.now());
        details.setUserId(userId);

        if (extractedData != null) {
            details.setName(extractedData.getName());
            details.setGender(extractedData.getGender());

            // --- DOB & Age ---
            if (extractedData.getDob() != null) {
                try {
                    LocalDate dob = LocalDate.parse(extractedData.getDob(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    details.setDob(dob);
                    details.setAge(Period.between(dob, LocalDate.now()).getYears());
                } catch (Exception e) {
                    details.setDob(null);
                    details.setAge(null);
                }
            }

            // --- Card Number Validation & Masking ---
            if (extractedData.getCardNumber() != null) {
                String rawCardNumber = extractedData.getCardNumber().replaceAll("\\s", "").toUpperCase();

                if ("Aadhar".equalsIgnoreCase(cardType)) {
                    if (rawCardNumber.matches("^\\d{12}$")) {
                        details.setCardNumber(maskAadhar(rawCardNumber));
                    } else {
                        details.setCardNumber(null);
                        details.setStatus("Rejected: Invalid Aadhar Number");
                        details.setSimEligibilityMessage("Invalid Aadhar number format.");
                        return documentRepository.save(details);
                    }
                } else if ("PAN".equalsIgnoreCase(cardType)) {
                    if (rawCardNumber.matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
                        details.setCardNumber(maskPan(rawCardNumber));
                    } else {
                        details.setCardNumber(null);
                        details.setStatus("Rejected: Invalid PAN Number");
                        details.setSimEligibilityMessage("Invalid PAN number format.");
                        return documentRepository.save(details);
                    }
                } else {
                    details.setCardNumber(rawCardNumber); // other cards
                }
            }
        }

        // --- Age Verification and Status ---
        if (details.getAge() != null) {
            if (details.getAge() < 18) {
                details.setStatus("Rejected: Underage");
                details.setSimEligibilityMessage("Not eligible for SIM card in India. Minimum age is 18.");
            } else if (details.getStatus() == null) {
                details.setStatus("Pending");
                details.setSimEligibilityMessage("Eligible for SIM card.");
            }
        } else if (details.getStatus() == null) {
            details.setStatus("Pending Verification");
            details.setSimEligibilityMessage("SIM eligibility cannot be determined as age could not be extracted.");
        }

        return documentRepository.save(details);
    }

    // --- Circuit Breaker Fallback ---
    public DocumentDetails ocrFallback(String cardType, MultipartFile image1, MultipartFile image2, Long userId, Throwable ex) {
        DocumentDetails details = new DocumentDetails();
        details.setUserId(userId);
        details.setCardType(cardType);
        details.setStatus("Pending Verification");
        details.setSimEligibilityMessage("Document processing temporarily unavailable. Please try again later.");
        details.setPreparedAt(LocalDateTime.now());
        return documentRepository.save(details);
    }

    // --- Helper methods ---
    private BufferedImage toBufferedImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                BufferedImage tempImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                ImageIO.write(tempImage, "png", os);
                image = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
            }
        }
        if (image == null) {
            throw new IOException("Could not read image as BufferedImage: " + file.getOriginalFilename());
        }
        return image;
    }

    private String maskAadhar(String aadhar) {
        return "xxxx-xxxx-" + aadhar.substring(8);
    }

    private String maskPan(String pan) {
        return "XXXXX" + pan.substring(5);
    }
}
