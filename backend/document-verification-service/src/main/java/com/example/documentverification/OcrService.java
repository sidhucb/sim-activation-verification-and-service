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
import java.time.LocalDateTime; // Import LocalDateTime
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;

@Service
public class OcrService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private LlmService llmService;

    // ADDED: userId parameter to match the method call from the controller
    public DocumentDetails processDocument(String cardType, MultipartFile image1, MultipartFile image2, Long userId)
            throws IOException, TesseractException, ExecutionException, InterruptedException {

        Tesseract tesseract = new Tesseract();
        String tessDataPath = System.getProperty("user.dir") + File.separator + "tessdata";
        File tessDataFolder = new File(tessDataPath);

        System.out.println("Attempting to set Tesseract datapath to: " + tessDataPath);

        if (!tessDataFolder.exists() || !tessDataFolder.isDirectory()) {
            System.err.println("Tesseract tessdata folder not found at: " + tessDataPath);
            throw new IOException("Tesseract language data folder not found or is not a directory. Please ensure 'tessdata' folder with 'eng.traineddata' is in the project root and is accessible.");
        }
        tesseract.setDatapath(tessDataPath);
        
        tesseract.setLanguage("eng");

        StringBuilder extractedText = new StringBuilder();
        try {
            extractedText.append(tesseract.doOCR(toBufferedImage(image1)));
            
            if (image2 != null && !image2.isEmpty()) {
                extractedText.append("\n").append(tesseract.doOCR(toBufferedImage(image2)));
            }
        } catch (IOException e) {
            throw new IOException("Error converting image to BufferedImage or processing: " + e.getMessage(), e);
        }

        ExtractedAadharData extractedData = llmService.extractDataWithLlm(extractedText.toString()).get();

        DocumentDetails details = new DocumentDetails();
        details.setCardType(cardType);
        details.setPreparedAt(LocalDateTime.now());
        
        // SETTING THE USER ID: This is the most important change
        details.setUserId(userId);
        
        if (extractedData != null) {
            details.setName(extractedData.getName());
            details.setCardNumber(extractedData.getCardNumber());
            details.setGender(extractedData.getGender());
            details.setAddress(extractedData.getAddress());
            
            if (extractedData.getDob() != null) {
                try {
                    LocalDate dob = LocalDate.parse(extractedData.getDob(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    details.setDob(dob);
                    int age = Period.between(dob, LocalDate.now()).getYears();
                    details.setAge(age);
                } catch (Exception e) {
                    System.err.println("Error parsing DOB from LLM output: " + extractedData.getDob() + " - " + e.getMessage());
                    details.setDob(null);
                    details.setAge(null);
                }
            }
        }
        
        // --- Age Verification and Status Setting ---
        if (details.getAge() != null) {
            if (details.getAge() < 18) {
                details.setStatus("Rejected: Underage");
                details.setSimEligibilityMessage("Not eligible for SIM card in India. According to the Indian Contract Act, 1872, only individuals aged 18 or above can enter into a legally binding contract.");
            } else {
                details.setStatus("Pending");
                details.setSimEligibilityMessage("Eligible for SIM card.");
            }
        } else {
            details.setStatus("Pending Verification");
            details.setSimEligibilityMessage("SIM eligibility cannot be determined as age could not be extracted.");
        }

        return documentRepository.save(details);
    }
    
    private BufferedImage toBufferedImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            System.err.println("Initial ImageIO.read failed or returned null for " + file.getOriginalFilename() + ". Attempting re-encode as PNG.");
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                byte[] fileBytes = file.getBytes();
                BufferedImage tempImage = ImageIO.read(new ByteArrayInputStream(fileBytes));
                if (tempImage != null) {
                    ImageIO.write(tempImage, "png", os);
                    image = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
                }
            } catch (Exception e) {
                System.err.println("Re-encoding as PNG failed: " + e.getMessage());
                image = ImageIO.read(file.getInputStream());
            }
        }
        if (image == null) {
            throw new IOException("Could not read image as BufferedImage after initial attempt and PNG re-encode for file: " + file.getOriginalFilename());
        }
        return image;
    }
}
