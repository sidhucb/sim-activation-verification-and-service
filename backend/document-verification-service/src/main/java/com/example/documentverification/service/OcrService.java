package com.example.documentverification.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        // Set tessdata path — ensure tessdata is present at this location or change accordingly
        this.tesseract.setDatapath("./tessdata");
        // Optional: set language by tesseract.setLanguage("eng");
    }

    /**
     * Extract raw text from an image file path
     * Throws TesseractException if OCR fails for any reason.
     */
    public String extractText(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);
        return tesseract.doOCR(imageFile);
    }
}
