package org.example;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;

public class Main {

    private static final int TOP_PANEL_HEIGHT = 400;
    private static final int BOTTOM_PANEL_HEIGHT = 200;
    private static final String TESSERACT_PATH = "/usr/share/tesseract-ocr/5/tessdata";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OCRScreen Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, TOP_PANEL_HEIGHT + BOTTOM_PANEL_HEIGHT + 50);

            // Split UI: Image on left, Text on right
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setDividerLocation(TOP_PANEL_HEIGHT);
            splitPane.setResizeWeight(0.0);
            splitPane.setEnabled(false);

            // Top Panel: Image and Buttons
            JPanel topPanel = new JPanel(new BorderLayout());
            CustomImageLabel imageLabel = new CustomImageLabel();
            topPanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);

            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton pasteButton = new JButton("Paste Screenshot (Ctrl+V)");
            JButton copyButton = new JButton("Copy Text (Ctrl+C)");
            buttonPanel.add(pasteButton);
            buttonPanel.add(copyButton);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Text Area for OCR result
            JTextArea textArea = new JTextArea();
            updateTextAreaAppearance(textArea);

            splitPane.setTopComponent(topPanel);
            splitPane.setBottomComponent(new JScrollPane(textArea));

            frame.add(splitPane, BorderLayout.CENTER);

            defineActionListeners(frame, pasteButton, copyButton, textArea, imageLabel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static String performTesseractOCR(BufferedImage image) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(Main.TESSERACT_PATH); // Update this path
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            return "Error during OCR processing: " + e.getMessage();
        }
    }
    private static void pasteImage(CustomImageLabel imageLabel, JTextArea textArea) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                BufferedImage image = (BufferedImage) contents.getTransferData(DataFlavor.imageFlavor);
                imageLabel.setImage(image);

                // Perform OCR
                String textAfterOcr = performTesseractOCR(image);
                textArea.setText(textAfterOcr);
            } catch (Exception e) {
                textArea.setText("Error during Image Paste: " + e.getMessage());
            }
        } else {
            textArea.setText("No image found in clipboard.");
        }
    }
    private static void copyText(JTextArea textArea) {
        String text = textArea.getText();
        if (!text.isEmpty()) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(text);
            clipboard.setContents(stringSelection, null);
        }
    }
    private static void updateTextAreaAppearance(JTextArea textArea){
        textArea.setEditable(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(textArea.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    };
    private static void defineActionListeners(JFrame frame, JButton pasteButton, JButton copyButton, JTextArea textArea, CustomImageLabel imageLabel) {
        // Keyboard shortcut for paste
        pasteButton.addActionListener(e -> pasteImage(imageLabel, textArea));
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control V"), "paste");
        frame.getRootPane().getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pasteImage(imageLabel, textArea);
            }
        });

        // Copy action
        copyButton.addActionListener(e -> copyText(textArea));
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control C"), "copy");
        frame.getRootPane().getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copyText(textArea);
            }
        });
    }
}