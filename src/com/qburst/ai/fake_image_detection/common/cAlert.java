package com.qburst.ai.fake_image_detection.common;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class cAlert {

    public static void showAlert(String title, String Content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(Content);
        alert.showAndWait();
    }
}
