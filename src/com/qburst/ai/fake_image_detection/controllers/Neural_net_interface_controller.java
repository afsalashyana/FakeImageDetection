package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.ai.fake_image_detection.neural_network.image_processor.error_level_analyzer;
import com.qburst.ai.fake_image_detection.neural_network.thread_sync.ThreadCompleteListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Neural_net_interface_controller implements Initializable, ThreadCompleteListener {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text description;
    @FXML
    private Text christopher;
    @FXML
    private JFXButton navigation_button;
    @FXML
    private ImageView homeIcon;

    public static String imageLocation = "";
    ScaleTransition bulgingTransition;

    public static String getImageLocation() {
        return imageLocation = Main_window_controller.processingFile.getAbsolutePath();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startAnimation();
        getImageLocation();

        error_level_analyzer elaAnalyzer = new error_level_analyzer(imageLocation, 95);
        elaAnalyzer.addListener(this);
        elaAnalyzer.start();

    }

    private void startAnimation() {
        navigation_button.setText("Applying ELA on Image");
        float animationExtension = 1.2f;
        bulgingTransition = new ScaleTransition(Duration.millis(2000), navigation_button);
        bulgingTransition.setToX(animationExtension);
        bulgingTransition.setToY(animationExtension);
        bulgingTransition.autoReverseProperty().setValue(true);
        bulgingTransition.setCycleCount(bulgingTransition.INDEFINITE);
        bulgingTransition.play();
    }

    @FXML
    private void loadAnimation(MouseEvent event) {
    }

    @FXML
    private void rollBack(MouseEvent event) {
        Main_window_controller.processingFile=null;
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene pane = new Scene(FXMLLoader.load(getClass().getResource("/resources/fxml/main_window.fxml")));
            stage.setScene(pane);
        } catch (IOException ex) {
            Logger.getLogger(Neural_net_interface_controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void updateIndicatorText(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                navigation_button.setText(message);
            }
        });
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        updateIndicatorText("Serializing Image");
    }

}
