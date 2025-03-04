package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

class CustomImageLabel extends JLabel {
    private BufferedImage originalImage;

    public CustomImageLabel() {
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        // Resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateImage();
            }
        });
    }

    public void setImage(BufferedImage image) {
        this.originalImage = image;
        updateImage();
    }

    private void updateImage() {
        if (originalImage == null) {
            setIcon(null);
            return;
        }

        // Get pane dimensions
        int paneWidth = getWidth();
        int paneHeight = getHeight();

        if (paneWidth <= 0 || paneHeight <= 0) {
            return;
        }

        // Calculate scaled dimensions while preserving aspect ratio
        double imgWidth = originalImage.getWidth();
        double imgHeight = originalImage.getHeight();
        double widthRatio = (double) paneWidth / imgWidth;
        double heightRatio = (double) paneHeight / imgHeight;
        double scale = Math.min(widthRatio, heightRatio); // Fit within pane

        int scaledWidth = (int) (imgWidth * scale);
        int scaledHeight = (int) (imgHeight * scale);

        // Scale the image
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        setIcon(icon);

        revalidate();
        repaint();
    }
}
