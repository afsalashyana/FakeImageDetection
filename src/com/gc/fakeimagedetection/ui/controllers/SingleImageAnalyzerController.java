package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.gc.fakeimagedetection.ui.alert.Calert;
import static com.gc.fakeimagedetection.core.processor.NeuralNetProcessor.nnet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;

public class SingleImageAnalyzerController implements Initializable {

    @FXML
    private JFXButton neuralSource;
    @FXML
    private JFXCheckBox nnIndicator;
    @FXML
    private JFXButton imgSource;
    @FXML
    private JFXCheckBox imgIndicator;
    @FXML
    private JFXButton startButton;

    File nnetSrc;
    File imgSrc;
    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void loadNeuralNetwork(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Neural Network");
        nnetSrc = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (nnetSrc != null) {
            nnIndicator.setSelected(true);
        }
    }

    @FXML
    private void loadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        imgSrc = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (imgSrc != null) {
            imgIndicator.setSelected(true);
        }
    }

    @FXML
    private void startCheck(ActionEvent event) throws IOException {
        if (nnetSrc == null || imgSrc == null) {
            Calert.showAlert("Invalid Data", "Select Required Files", Alert.AlertType.ERROR);
            return;
        }
        try {
            nnet = NeuralNetwork.load(new FileInputStream(nnetSrc)); // load trained neural network saved with Neuroph Studio
            System.out.println("Learning Rule = " + nnet.getLearningRule());
            ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) nnet.getPlugin(ImageRecognitionPlugin.class); // get the 
            HashMap<String, Double> output = imageRecognition.recognizeImage(ImageIO.read(imgSrc));
            if (output == null) {
                System.err.println("Image Recognition Failed");
            }
            double real = output.get("real");
            double fake = output.get("faked");
            System.out.println(output.toString());
            Calert.showAlert("Result", "Real = " + real + "\nFake = " + fake, Alert.AlertType.INFORMATION);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SingleImageAnalyzerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
