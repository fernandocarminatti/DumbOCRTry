module com.dumbtest.ocrapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires tess4j;
    requires java.desktop;


    opens com.dumbtest.ocrapp to javafx.fxml;
    exports com.dumbtest.ocrapp;
}