package com.example.documentverification;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrService.class);

    private final DocumentRepository documentRepository;
    private final Tesseract tesseract;
    private final Tika tika;

    public OcrService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        this.tesseract = new Tesseract();
        // Crucial: The "tessdata" folder must be accessible to the application.
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        this.tika = new Tika();
    }

    public DocumentDetails processDocument(String cardType, MultipartFile image1, MultipartFile image2)
            throws IOException, TesseractException {

        StringBuilder extractedText = new StringBuilder();
        File tempFile1 = null;
        File tempFile2 = null;

        try {
            log.info("Starting OCR process for cardType: {}", cardType);
            
            // Step 1: Convert and OCR image1
            log.debug("Converting and OCR-ing image1...");
            tempFile1 = convertToTempFile(image1);
            String ocrResult1 = tesseract.doOCR(tempFile1);
            extractedText.append(ocrResult1);
            log.info("Successfully extracted text from image1 ({} characters).", ocrResult1.length());

            // Step 2: Convert and OCR image2 if it exists
            if (image2 != null && !image2.isEmpty()) {
                log.debug("Converting and OCR-ing image2...");
                tempFile2 = convertToTempFile(image2);
                String ocrResult2 = tesseract.doOCR(tempFile2);
                extractedText.append("\n").append(ocrResult2);
                log.info("Successfully extracted text from image2 ({} characters).", ocrResult2.length());
            }

        } catch (IOException | TesseractException e) {
            log.error("An error occurred during OCR or file conversion.", e);
            throw e; // Rethrow to let the controller handle it.
        } finally {
            // Ensure temp files are deleted, regardless of success or failure
            if (tempFile1 != null && tempFile1.exists()) {
                tempFile1.delete();
                log.debug("Deleted temporary file: {}", tempFile1.getName());
            }
            if (tempFile2 != null && tempFile2.exists()) {
                tempFile2.delete();
                log.debug("Deleted temporary file: {}", tempFile2.getName());
            }
        }
        
        log.debug("Full extracted text:\n{}", extractedText.toString());

        DocumentDetails details = new DocumentDetails();
        details.setCardType(cardType);

        // Step 3: Parse extracted details based on card type
        if ("aadhaar".equalsIgnoreCase(cardType)) {
            parseAadhaarDetails(extractedText.toString(), details);
        } else if ("pancard".equalsIgnoreCase(cardType) || "pan".equalsIgnoreCase(cardType)) {
            parsePanDetails(extractedText.toString(), details);
        }

        // Step 4: Calculate age and set status
        if (details.getDob() != null) {
            int age = Period.between(details.getDob(), LocalDate.now()).getYears();
            details.setAge(age);
            details.setStatus(age < 18 ? "Rejected: Underage" : "Verified");
        } else {
            details.setStatus("Pending Verification: DOB not found");
        }

        // Handle case where no name was found
        if (details.getName() == null || details.getName().isEmpty()) {
            details.setStatus("Pending Verification: Name not found");
        }

        // Step 5: Save to database
        return documentRepository.save(details);
    }
    
    // The rest of the private helper methods remain the same as in the previous response.
    // ... (parseAadhaarDetails, parsePanDetails, parseDate, convertToTempFile, isSupportedMimeType)
    
    private void parseAadhaarDetails(String text, DocumentDetails details) {
        log.debug("Parsing Aadhaar details from text...");
        // Aadhaar: Extract Name
        Matcher nameMatcher = Pattern.compile("Name: ([A-Za-z\\s]+)").matcher(text);
        if (nameMatcher.find()) {
            details.setName(nameMatcher.group(1).trim());
        } else {
            for (String line : text.split("\\n")) {
                String trimmedLine = line.trim();
                if (trimmedLine.matches("[A-Z\\s]+")) {
                    details.setName(trimmedLine);
                    break;
                }
            }
        }
        // Aadhaar: Extract DOB
        Matcher dobMatcher = Pattern.compile("DOB: (\\d{2}/\\d{2}/\\d{4})").matcher(text);
        if (dobMatcher.find()) {
            details.setDob(parseDate(dobMatcher.group(1), "dd/MM/yyyy"));
        } else {
             dobMatcher = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})").matcher(text);
             if (dobMatcher.find()) details.setDob(parseDate(dobMatcher.group(1), "dd/MM/yyyy"));
        }
        // Aadhaar: Extract Gender
        Matcher genderMatcher = Pattern.compile("Gender: ([a-zA-Z]+)").matcher(text);
        if (genderMatcher.find()) {
            details.setGender(genderMatcher.group(1).trim());
        } else {
            if (text.toLowerCase().contains("male")) details.setGender("Male");
            if (text.toLowerCase().contains("female")) details.setGender("Female");
        }
        // Aadhaar: Extract Card Number
        Matcher numberMatcher = Pattern.compile("(\\d{4}\\s\\d{4}\\s\\d{4})|(\\d{12})").matcher(text);
        if (numberMatcher.find()) {
            details.setCardNumber(numberMatcher.group().replaceAll("\\s", ""));
        }
        // Aadhaar: Extract Address
        Matcher addressMatcher = Pattern.compile("Address: (.+)").matcher(text);
        if(addressMatcher.find()) {
            details.setAddress(addressMatcher.group(1).trim());
        }
    }

    private void parsePanDetails(String text, DocumentDetails details) {
        log.debug("Parsing PAN card details from text:\n{}", text);
        // PAN Card: Extract Name (first line of alphabetic characters)
        List<String> lines = Arrays.stream(text.split("\\n"))
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .collect(Collectors.toList());
        for (String line : lines) {
            if (line.matches("^[A-Z\\s]+$")) {
                details.setName(line);
                break;
            }
        }
        // PAN Card: Extract DOB
        Matcher dobMatcher = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})").matcher(text);
        if (dobMatcher.find()) {
            details.setDob(parseDate(dobMatcher.group(1), "dd/MM/yyyy"));
        }
        // PAN Card: Extract PAN Number
        Matcher panMatcher = Pattern.compile("[A-Z]{5}\\d{4}[A-Z]").matcher(text);
        if (panMatcher.find()) {
            details.setCardNumber(panMatcher.group());
        }
    }


    private LocalDate parseDate(String dateStr, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date '{}' with pattern '{}'", dateStr, pattern);
            return null;
        }
    }

    private File convertToTempFile(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        if (bufferedImage == null) {
            throw new IOException("Unsupported image format or corrupted file. ImageIO.read returned null.");
        }
        File tempFile = File.createTempFile("ocr_", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);
        log.debug("Converted MultipartFile to temp file: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    public boolean isSupportedMimeType(MultipartFile file) throws IOException {
        String mimeType = tika.detect(file.getInputStream());
        log.debug("Detected MIME type: {}", mimeType);
        return mimeType.equalsIgnoreCase("image/jpeg") ||
               mimeType.equalsIgnoreCase("image/png") ||
               mimeType.equalsIgnoreCase("image/bmp") ||
               mimeType.equalsIgnoreCase("application/pdf") ||
               mimeType.equalsIgnoreCase("image/tiff");
    }
}