package com.cardactivation;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();

        // ✅ Use system property or application.properties instead of hardcoding
        String tessDataPath = System.getProperty("tessdata.path", "D:\\Capstone\\SimCardActivation\\tessdata");
        this.tesseract.setDatapath(tessDataPath);

        this.tesseract.setLanguage("eng"); // multiple: "eng+hin"
    }

    public String extractText(MultipartFile file) throws IOException, TesseractException {
        File tempFile = File.createTempFile("ocr-", ".png"); // safer naming
        try {
            file.transferTo(tempFile);
            return tesseract.doOCR(tempFile);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete(); // cleanup always
            }
        }
    }
}
