package org.example;

import net.sourceforge.tess4j.util.ImageHelper;

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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OCR Screen Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, TOP_PANEL_HEIGHT + BOTTOM_PANEL_HEIGHT + 20);

            // Split UI: Image on top, Text on bottom
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

            // Set component for SplitUI top and bottom
            splitPane.setTopComponent(topPanel);
            splitPane.setBottomComponent(new JScrollPane(textArea));

            // Set component for Frame
            frame.add(splitPane, BorderLayout.CENTER);

            // Define action listeners
            defineActionListeners(frame, pasteButton, copyButton, textArea, imageLabel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void pasteImageAndPerformOCR(CustomImageLabel imageLabel, JTextArea textArea) {
        try {
            BufferedImage defaultImageFile = getImageFromClipboard();
            String extractedText = TesseractService.performOCRProcessing(defaultImageFile);
            textArea.setText(extractedText);
            imageLabel.setImage(defaultImageFile);
        } catch (Exception e) {
            textArea.setText(e.getMessage());
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

    private static BufferedImage getImageFromClipboard(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents == null || !contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            throw new IllegalArgumentException("No image found in clipboard.");
        }
        return (BufferedImage) ImageHelper.getClipboardImage();
    }

    private static void updateTextAreaAppearance(JTextArea textArea){
        textArea.setEditable(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(textArea.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
    private static void defineActionListeners(JFrame frame, JButton pasteButton, JButton copyButton, JTextArea textArea, CustomImageLabel imageLabel) {
        // Keyboard shortcut for paste
        pasteButton.addActionListener(e -> pasteImageAndPerformOCR(imageLabel, textArea));
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control V"), "paste");
        frame.getRootPane().getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pasteImageAndPerformOCR(imageLabel, textArea);
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