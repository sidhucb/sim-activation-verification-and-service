package com.example.documentverification.service;

import com.example.documentverification.model.KycData;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    private final String tessDataPath = "C:/kyc/sim-activation-verification-and-service/backend/document-verification-service/tessdata"; // adjust to your env
    private final ITesseract tesseract;
    private final TextractClient textract;
    private final AiParsingService aiParsingService;

    public OcrService(AiParsingService aiParsingService) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(tessDataPath);
        this.tesseract.setLanguage("eng");

        this.textract = TextractClient.builder()
                .region(Region.US_EAST_1)
                .build();

        this.aiParsingService = aiParsingService;
    }

    public KycData extract(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Empty file provided");
        }

        // Save temp file
        File tmp = File.createTempFile("kyc_upload_", ".tmp");
        file.transferTo(tmp);

        KycData data = null;

        try {
            // 1️⃣ Try Tesseract local OCR
            String localText = tryLocalOcr(tmp);
            data = parseKycDataFromText(localText);

            // 2️⃣ If local OCR fails, try AWS Textract
            if (!isDataValid(data)) {
                String cloudText = tryTextract(tmp);
                data = parseKycDataFromText(cloudText);
            }

            // 3️⃣ If still invalid, try AI parsing
            if (!isDataValid(data)) {
                String combinedText = tryLocalOcr(tmp) + "\n" + tryTextract(tmp);
                KycData aiData = aiParsingService.parseTextWithAi(combinedText);
                if (aiData != null) data = aiData;
            }

        } finally {
            try { Files.deleteIfExists(tmp.toPath()); } catch (Exception ignored) {}
        }

        if (!isDataValid(data)) {
            throw new IOException("OCR and AI parsing failed to extract required KYC fields");
        }

        return data;
    }

    private String tryLocalOcr(File imageFile) {
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            return "";
        }
    }

    private String tryTextract(File imageFile) {
        try {
            byte[] bytes = Files.readAllBytes(imageFile.toPath());
            SdkBytes sdkBytes = SdkBytes.fromByteArray(bytes);
            Document doc = Document.builder().bytes(sdkBytes).build();

            AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                    .featureTypes(FeatureType.FORMS)
                    .document(doc)
                    .build();

            AnalyzeDocumentResponse response = textract.analyzeDocument(request);
            StringBuilder sb = new StringBuilder();
            response.blocks().forEach(b -> {
                if (b.blockType() == BlockType.LINE && b.text() != null) {
                    sb.append(b.text()).append("\n");
                }
            });
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private KycData parseKycDataFromText(String ocrText) {
        if (ocrText == null || ocrText.isBlank()) return new KycData();

        String normalized = ocrText.replaceAll("[\r\n]+", "\n").trim();

        String name = extractName(normalized);
        LocalDate dob = extractDob(normalized);
        String cardNumber = extractCardNumber(normalized);
        String cardType = determineCardType(cardNumber, normalized);

        KycData data = new KycData();
        data.setName(name);
        data.setDob(dob);
        data.setCardNumber(cardNumber);
        data.setCardType(cardType);

        return data;
    }

    private boolean isDataValid(KycData d) {
        return d != null && d.getName() != null && d.getDob() != null &&
               d.getCardNumber() != null && d.getCardType() != null &&
               !"UNKNOWN".equalsIgnoreCase(d.getCardType());
    }

    // ---------- Helper methods (Name, DOB, Card extraction) ----------
    private String extractName(String text) {
        if (text == null) return null;
        String[] lines = text.split("\n");
        String candidate = null;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() < 3 || trimmed.length() > 100) continue;
            if (trimmed.matches(".*\\d.*")) continue;
            if (trimmed.matches("^[A-Z ]+$") || trimmed.matches("^[A-Z][a-z]+( [A-Z][a-z]+)+$")) {
                return trimmed;
            }
            if (candidate == null && trimmed.split(" ").length >= 2) candidate = trimmed;
        }
        return candidate;
    }

    private LocalDate extractDob(String text) {
        if (text == null) return null;
        List<Pattern> patterns = Arrays.asList(
            Pattern.compile("(\\b\\d{2}[\\-/]\\d{2}[\\-/]\\d{4}\\b)"),
            Pattern.compile("(\\b\\d{4}[\\-/]\\d{2}[\\-/]\\d{2}\\b)")
        );
        for (Pattern p : patterns) {
            Matcher m = p.matcher(text);
            if (m.find()) {
                LocalDate d = tryParseDate(m.group(1));
                if (d != null) return d;
            }
        }
        return null;
    }

    private LocalDate tryParseDate(String s) {
        List<DateTimeFormatter> fmts = Arrays.asList(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
        );
        for (DateTimeFormatter f : fmts) {
            try { return LocalDate.parse(s, f); } catch (Exception ignored) {}
        }
        return null;
    }

    private String extractCardNumber(String text) {
        if (text == null) return null;
        Pattern pan = Pattern.compile("\\b([A-Z]{5}[0-9]{4}[A-Z])\\b");
        Matcher mPan = pan.matcher(text.replaceAll("\\s+", ""));
        if (mPan.find()) return mPan.group(1);

        Pattern aadhar = Pattern.compile("\\b(\\d{4}\\s?\\d{4}\\s?\\d{4})\\b");
        Matcher mAad = aadhar.matcher(text);
        if (mAad.find()) return mAad.group(1).replaceAll("\\s+", "");

        return null;
    }

    private String determineCardType(String cardNumber, String text) {
        if (cardNumber == null) return "UNKNOWN";
        if (cardNumber.matches("[A-Z]{5}[0-9]{4}[A-Z]")) return "PAN";
        if (cardNumber.matches("\\d{12}")) return "AADHAAR";
        String up = text.toUpperCase();
        if (up.contains("AADHAAR") || up.contains("UIDAI")) return "AADHAAR";
        if (up.contains("PAN") || up.contains("PERMANENT ACCOUNT NUMBER")) return "PAN";
        return "UNKNOWN";
    }
}
