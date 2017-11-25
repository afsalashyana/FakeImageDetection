package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.gc.fakeimagedetection.core.listener.BatchImageTestingListener;
import com.gc.fakeimagedetection.core.tester.BatchImageTestProcessor;
import com.gc.fakeimagedetection.ui.alert.Calert;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class BatchImageTesterController implements Initializable, BatchImageTestingListener {

    @FXML
    private JFXCheckBox neuralIndicator;
    @FXML
    private JFXCheckBox realIndicator;
    @FXML
    private JFXCheckBox fakeIndicator;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton startButton;

    File neuralNetwork;
    File realDir;
    File fakeDir;
    @FXML
    private JFXSpinner spinner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spinner.setVisible(false);
    }

    @FXML
    private void chooseNeuralNetwork(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Neuralnet");
        neuralNetwork = chooser.showOpenDialog(rootPane.getScene().getWindow());
        if (neuralNetwork == null) {
            Calert.showAlert("Error", "Not a valid neural network", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Loading Neuralnet from " + neuralNetwork.getAbsolutePath());
        neuralIndicator.setSelected(true);
    }

    @FXML
    private void chooseRealImageSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Real Image Folder");
        realDir = chooser.showDialog(rootPane.getScene().getWindow());
        if (realDir == null) {
            Calert.showAlert("Error", "Not a valid directory", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Loading Real Images from " + realDir.getAbsolutePath());
        realIndicator.setSelected(true);
    }

    @FXML
    private void chooseFakeImageSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Fake Image Folder");
        fakeDir = chooser.showDialog(rootPane.getScene().getWindow());
        if (fakeDir == null) {
            Calert.showAlert("Error", "Not a valid directory", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Loading Fake Images from " + realDir.getAbsolutePath());
        fakeIndicator.setSelected(true);
    }

    @FXML
    private void startTesting(ActionEvent event) {
        startButton.setText("Running...");
        BatchImageTestProcessor processor = new BatchImageTestProcessor(neuralNetwork, realDir, fakeDir);
        processor.setListener(this);
        Thread runnerThread = new Thread(processor);
        runnerThread.start();
        spinner.setVisible(true);
    }

    void updateText(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startButton.setText(text);
            }
        });
    }

    void showResult(String result) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Calert.showAlert("Result", result, Alert.AlertType.INFORMATION);
            }
        });
    }

    @Override
    public void testingComplete(String result) {
        spinner.setVisible(false);
        showResult(result);
        updateText("Completed");
    }
}
