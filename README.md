# OCR Screen Tool

## Overview
OCR Screen Tool is a Java Swing application that allows users to paste screenshots and extract text using Tesseract OCR technology. The tool provides a simple, user-friendly interface for capturing and converting images to text.

## Features
- Paste screenshots directly from clipboard
- Perform OCR on pasted images
- Copy extracted text to clipboard
- Keyboard shortcuts for easy interaction
- Split-screen layout with image preview and text result

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- Tesseract OCR 5.x
- Tess4J library
- System clipboard access

## Dependencies
- Tess4J (Tesseract Java API)
- Swing for GUI
- Java AWT for clipboard and system interactions

## Installation
1. Clone the repository
2. Ensure Tesseract OCR is installed
3. Update `TESSERACT_PATH` in the `Main` class to match your Tesseract data path
4. Add required libraries to your project classpath
5. Build the project using Maven
6. Run "mvn clean package" to create runnable JAR file containing all dependencies
7. Run "java -jar target/ocr-tool-1.0.jar" to launch the application or create your own way to run it.

## Usage
1. Launch the application
2. Copy a screenshot to your clipboard
3. Click "Paste Screenshot" or use `Ctrl+V`
4. View extracted text in the bottom panel
5. Copy text using "Copy Text" button or `Ctrl+C`

## Keyboard Shortcuts
- `Ctrl+V`: Paste Screenshot
- `Ctrl+C`: Copy Extracted Text

## Troubleshooting
- Ensure Tesseract OCR is correctly installed
- Verify the `TESSERACT_PATH` points to the correct Tesseract data directory
- Check that required libraries are in the classpath
