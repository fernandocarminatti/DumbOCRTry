package com.dumbtest.ocrapp;

import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class OCRService {
    private static final Tesseract TESSERACT = new Tesseract();

    public static String extractText(String imagePath) {
        TESSERACT.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        try{
            System.out.println("Extracting text from image: " + imagePath);
            BufferedImage image = ImageIO.read(new File(imagePath));
            return TESSERACT.doOCR(image);
        } catch (Exception e) {
            return "Error: " + e;
        }
    }
}
