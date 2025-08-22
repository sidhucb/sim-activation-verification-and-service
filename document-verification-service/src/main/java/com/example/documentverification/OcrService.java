package com.example.documentverification;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    @Autowired
    private DocumentRepository documentRepository;

    public DocumentDetails processDocument(String cardType, MultipartFile image1, MultipartFile image2)
            throws IOException, TesseractException {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Path to tessdata folder
        tesseract.setLanguage("eng");

        // Extract text from images
        StringBuilder extractedText = new StringBuilder();
        extractedText.append(tesseract.doOCR(convert(image1)));
        if (image2 != null && !image2.isEmpty()) {
            extractedText.append("\n").append(tesseract.doOCR(convert(image2)));
        }

        String text = extractedText.toString();
        DocumentDetails details = new DocumentDetails();
        details.setCardType(cardType);

        // Parse text based on card type
        if (cardType.equalsIgnoreCase("aadhaar")) {
            parseAadhaarDetails(text, details);
        } else if (cardType.equalsIgnoreCase("pan")) {
            parsePanDetails(text, details);
        }

        // --- Age Verification ---
        if (details.getDob() != null) {
            int age = Period.between(details.getDob(), LocalDate.now()).getYears();
            if (age < 18) {
                details.setStatus("Rejected: Underage");
                return documentRepository.save(details);
            }
        }

        details.setStatus("Verified");
        return documentRepository.save(details);
    }

    // --- Aadhaar Parsing ---
    private void parseAadhaarDetails(String text, DocumentDetails details) {
        // Extract Aadhaar number
        Matcher aadhaarMatcher = Pattern.compile("\\b\\d{4}\\s\\d{4}\\s\\d{4}\\b").matcher(text);
        if (aadhaarMatcher.find()) {
            details.setCardNumber(aadhaarMatcher.group());
        }

        // Extract DOB
        Matcher dobMatcher = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})").matcher(text);
        if (dobMatcher.find()) {
            String dobStr = dobMatcher.group();
            LocalDate dob = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            details.setDob(dob);
        }

        // Extract Name
        String[] lines = text.split("\\n");
        if (lines.length > 1) {
            details.setName(lines[0].trim());
        }
    }

    // --- PAN Parsing ---
    private void parsePanDetails(String text, DocumentDetails details) {
        // Extract PAN number
        Matcher panMatcher = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}").matcher(text);
        if (panMatcher.find()) {
            details.setCardNumber(panMatcher.group());
        }

        // Extract Name
        Matcher nameMatcher = Pattern.compile("([A-Z]+\\s[A-Z]+)").matcher(text);
        if (nameMatcher.find()) {
            details.setName(nameMatcher.group());
        }

        // Extract DOB
        Matcher dobMatcher = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})").matcher(text);
        if (dobMatcher.find()) {
            String dobStr = dobMatcher.group();
            LocalDate dob = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            details.setDob(dob);
        }
    }

    // --- Convert MultipartFile to File ---
    private File convert(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("ocr_", ".tmp");
        Files.write(convFile.toPath(), file.getBytes());
        return convFile;
    }
}
