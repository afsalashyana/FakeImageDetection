package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.ai.fake_image_detection.neural_network.image_processor.error_level_analyzer;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Neural_net_interface_controller implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startAnimation();
        error_level_analyzer elaAnalyzer = new error_level_analyzer(imageLocation, 95);
        Thread elaMaker = new Thread(elaAnalyzer);
        elaMaker.start();
//        try {
//            navigation_button.setText("Image Processing Completed");
//        } catch (Exception ex) {
//            Logger.getLogger(Neural_net_interface_controller.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        BufferedImage filteredImage = elaAnalyzer.getFilteredImage();
//        if(filteredImage==null)
//        {
//            System.err.println("Returned Null Image");
//        }
    }

    private void startAnimation() {
        float animationExtension = 1f;
        ScaleTransition bulgingTransition = new ScaleTransition(Duration.millis(1000), navigation_button);
        bulgingTransition.setToX(animationExtension);
        bulgingTransition.setToY(animationExtension);
        bulgingTransition.autoReverseProperty().setValue(true);
        bulgingTransition.setCycleCount(2);
        bulgingTransition.play();
    }

    @FXML
    private void loadAnimation(MouseEvent event) {
    }


}
