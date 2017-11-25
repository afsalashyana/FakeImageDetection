package com.gc.fakeimagedetection.ui.alert;

import javafx.scene.control.Alert;

public class Calert {

    public static void showAlert(String title, String Content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(Content);
        alert.showAndWait();
    }
}
