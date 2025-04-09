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

    private static final int TOP_PANEL_HEIGHT = 200;
    private static final int BOTTOM_PANEL_HEIGHT = 400;

    private static final Font FONT_CONSOLAS = new Font("Consolas", Font.PLAIN, 14);

    private static final Insets TEXT_MARGIN_AROUND = new Insets(10, 10, 10, 10);
    private static final Color GLOBAL_BACKGROUND_COLOR = new Color(23, 23, 23);
    private static final Color TEXT_AREA_BACKGROUND_COLOR = new Color(23, 23, 23);
    private static final Color TEXT_COLOR = new Color(237, 237, 237);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o LookAndFeel: " + e.getMessage());
            e.getStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OCR Screen Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setOpacity(0.9f);
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
            JButton refreshAllButton = new JButton("Flush Data (Ctrl+R)");
            buttonPanel.add(pasteButton);
            buttonPanel.add(copyButton);
            buttonPanel.add(refreshAllButton);
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
            defineActionListeners(frame, pasteButton, copyButton, textArea, imageLabel, refreshAllButton);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void pasteImageAndPerformOCR(CustomImageLabel imageLabel, JTextArea textArea) {
        try {
            BufferedImage defaultImageFile = getImageFromClipboard();
            String extractedText = TesseractService.performOCRProcessing(defaultImageFile);
            textArea.append(extractedText);
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

    private static void refreshAll(CustomImageLabel imageLabel, JTextArea textArea) {
        imageLabel.setImage(null);
        textArea.setText("");
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
        textArea.setFont(FONT_CONSOLAS);
        textArea.setMargin(TEXT_MARGIN_AROUND);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    private static void defineActionListeners(JFrame frame, JButton pasteButton, JButton copyButton, JTextArea textArea, CustomImageLabel imageLabel, JButton refreshAllButton) {
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

        // Refresh all action
        refreshAllButton.addActionListener(e -> refreshAll(imageLabel, textArea));
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control R"), "refreshAll");
    }
}