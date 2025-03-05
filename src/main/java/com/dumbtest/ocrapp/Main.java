package com.dumbtest.ocrapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private ImageView imageView;
    private TextArea textArea;
    private VBox vbox;

    @Override
    public void start(Stage primaryStage) {

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);

        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);
        textArea.setPrefHeight(300);

        vbox = new VBox();
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setSpacing(20);

        VBox topRow = new VBox();
        topRow.setStyle("-fx-alignment: center;");
        topRow.getChildren().add(imageView);

        VBox bottomRow = new VBox();
        bottomRow.getChildren().add(textArea);

        vbox.getChildren().addAll(topRow, bottomRow);

        Button restartButton = new Button("Select Another Image");
        restartButton.setOnAction(e -> restartProgram(primaryStage));

        vbox.getChildren().add(restartButton);

        // Set up the Scene
        Scene scene = new Scene(vbox, 1000, 600);
        primaryStage.setTitle("Image Chooser App");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadImageAndOCR(primaryStage);
    }

    private void loadImageAndOCR(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            String imagePath = file.toURI().toString();
            Image image = new Image(imagePath);
            imageView.setImage(image);
            String extractedText = OCRService.extractText(file.getAbsolutePath());
            textArea.setText(extractedText);
        }
    }

    private void restartProgram(Stage primaryStage) {
        textArea.clear();
        loadImageAndOCR(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
