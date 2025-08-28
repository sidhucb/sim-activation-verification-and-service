package com.example.documentverification;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRService {

    public String extractText(File file) throws TesseractException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // path to tessdata folder
        tesseract.setLanguage("eng");
        return tesseract.doOCR(file);
    }
}
