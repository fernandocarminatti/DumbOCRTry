package org.example;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

import java.awt.image.BufferedImage;

public class TesseractService {

    private static final Tesseract TESSERACT = new Tesseract();

    static {
        configureTesseract();
    }

    private static void configureTesseract() {
        TESSERACT.setDatapath("/usr/share/tesseract/tessdata");
        TESSERACT.setLanguage("por");
        TESSERACT.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);
        TESSERACT.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
    }

    public static String performOCRProcessing(BufferedImage image) {
        BufferedImage preprocessedImage = preprocessImage(image);
        try {
            return TESSERACT.doOCR(preprocessedImage);
        } catch (TesseractException e) {
            return "Erro durante o OCR. Tente novamente.";
        }
    }

    private static BufferedImage preprocessImage(BufferedImage image) {
        int scaleFactor = 3;
        int scaledWidth = image.getWidth() * scaleFactor;
        int scaledHeight = image.getHeight() * scaleFactor;

        BufferedImage scaledImage = ImageHelper.getScaledInstance(image, scaledWidth, scaledHeight);
        BufferedImage grayscaleImage = ImageHelper.convertImageToGrayscale(scaledImage);
        return grayscaleImage;
    }
}