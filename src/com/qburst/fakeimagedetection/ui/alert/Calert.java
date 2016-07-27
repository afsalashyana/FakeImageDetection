package com.qburst.fakeimagedetection.ui.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Calert {

    public static void showAlert(String title, String Content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(Content);
        alert.showAndWait();
    }
}
