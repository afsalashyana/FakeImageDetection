package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.qburst.ai.fake_image_detection.neural_network.core.training.Nn_trainer;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class Training_interfaceController implements Initializable {

    @FXML
    private JFXButton sourceIndicator;
    @FXML
    private CheckBox srcIndicator;
    @FXML
    private JFXTextField realLabel;
    @FXML
    private JFXTextField fakeLabel;
    @FXML
    private JFXButton startButton;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField width;
    @FXML
    private JFXTextField height;
    @FXML
    private JFXTextField learningRate;
    @FXML
    private JFXTextField momentum;
    @FXML
    private JFXTextField maxError;

    ArrayList<String> imageLabels;
    File srcDir = null;
    String rLabel = "";
    String fLabel = "";
    @FXML
    private LineChart<Integer, Double> errorChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;

    XYChart.Series series;
    Nn_trainer neuralTrainer;
    @FXML
    private JFXButton saveButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageLabels = new ArrayList<>();
        series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        errorChart.getData().add(series);
    }

    public void notifyLearningCompleted() {
        saveButton.setDisable(false);
    }

    @FXML
    private void loadImageSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Traing Set");
        srcDir = chooser.showDialog(rootPane.getScene().getWindow());
        System.out.println("Loading Dataset from " + srcDir.getAbsolutePath());
        srcIndicator.setSelected(true);
    }

    @FXML
    private void startTraining(ActionEvent event) {
        switch (startButton.getAccessibleText()) {
            case "start":
                rLabel = realLabel.getText();
                fLabel = fakeLabel.getText();
                imageLabels.add(rLabel);
                imageLabels.add(fLabel);
                int sampledWidth = Integer.parseInt(width.getText());
                int sampledheight = Integer.parseInt(height.getText());
                float lRate = Float.parseFloat(learningRate.getText());
                float moment = Float.parseFloat(momentum.getText());
                float mError = Float.parseFloat(maxError.getText());

                System.out.println("Starting training procedure");
                neuralTrainer = new Nn_trainer(srcDir,
                        new Dimension(sampledWidth, sampledWidth), imageLabels, series, this);
                neuralTrainer.setMaxError(mError);
                neuralTrainer.setMomentum(moment);
                neuralTrainer.setLearningRate(lRate);
                neuralTrainer.start();

                startButton.setAccessibleText("stop");
                startButton.setStyle("-fx-background-color:#e53935;-fx-text-fill:#ffffff");
                startButton.setText("Stop Training");
                break;
            case "stop":
                neuralTrainer.stopLearning();
                startButton.setAccessibleText("stopped");
                startButton.setText("Stopped");
                break;
            default:
                System.out.println("Invalid Accessible Text");

        }

    }

    @FXML
    private void saveLearnedNetwork(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Neural Network");
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        neuralTrainer.saveLearnedNetwork(file.getAbsolutePath());
    }

}
