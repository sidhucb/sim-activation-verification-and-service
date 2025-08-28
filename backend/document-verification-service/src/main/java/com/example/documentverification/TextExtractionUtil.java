package com.example.documentverification;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class TextExtractionUtil {

    public static String extractText(File file) throws TesseractException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        return tesseract.doOCR(file);
    }
}
