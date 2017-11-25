package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.gc.fakeimagedetection.core.listener.BatchImageTrainingListener;
import com.gc.fakeimagedetection.ui.alert.Calert;
import com.gc.fakeimagedetection.core.trainer.BatchImageTrainer;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TrainingInterfaceController implements
        Initializable, BatchImageTrainingListener {

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
    @FXML
    private LineChart<Integer, Double> errorChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton neuralSource;
    @FXML
    private JFXCheckBox nnIndicator;
    @FXML
    private Pane containerPlane;
    @FXML
    private JFXSpinner spinner;
    
    
    ArrayList<String> imageLabels;
    File srcDir = null;
    File nnFile = null;
    String rLabel = "";
    String fLabel = "";
    XYChart.Series series;
    BatchImageTrainer neuralTrainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageLabels = new ArrayList<>();
        series = new XYChart.Series();
        series.setName("Learning Curve 1");
        errorChart.getData().add(series);

        spinner.setVisible(false);
    }

    
    @FXML
    private void loadImageSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Traing Set");
        srcDir = chooser.showDialog(rootPane.getScene().getWindow());
        if (srcDir == null) {
            Calert.showAlert("Error", "Not a valid folder", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Loading Dataset from " + srcDir.getAbsolutePath());
        srcIndicator.setSelected(true);
    }

    @FXML
    private void startTraining(ActionEvent event) {
        switch (startButton.getAccessibleText()) {
            case "start":
                try {
                    rLabel = realLabel.getText();
                    fLabel = fakeLabel.getText();
                    imageLabels.add(fLabel);
                    imageLabels.add(rLabel);
                    int sampledWidth = Integer.parseInt(width.getText());
                    int sampledheight = Integer.parseInt(height.getText());
                    float lRate = Float.parseFloat(learningRate.getText());
                    float moment = Float.parseFloat(momentum.getText());
                    float mError = Float.parseFloat(maxError.getText());
                    if (srcDir == null || nnFile == null) {
                        Calert.showAlert("Incomplete Configuration Data", "Check parameters", Alert.AlertType.ERROR);
                        return;
                    }

                    System.out.println("Starting training procedure");
                    neuralTrainer = new BatchImageTrainer(srcDir, nnFile,
                            new Dimension(sampledWidth, sampledWidth), imageLabels, this);
                    neuralTrainer.setMaxError(mError);
                    neuralTrainer.setMomentum(moment);
                    neuralTrainer.setLearningRate(lRate);
                    neuralTrainer.start();
                    spinner.setVisible(true);

                    startButton.setAccessibleText("stop");
                    startButton.setStyle("-fx-background-color:#e53935;-fx-text-fill:#ffffff");
                    startButton.setText("Stop Training");
                } catch (Exception e) {
                    Calert.showAlert("Incomplete Configuration Data", e.getMessage(), Alert.AlertType.ERROR);
                }
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

    @FXML
    private void loadNeuralNetwork(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Neural Nets", "*.nnet"));
        nnFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if (nnFile == null) {
            Calert.showAlert("Error", "Not a valid neural network", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Training Neural " + nnFile.getAbsolutePath());
        nnIndicator.setSelected(true);
    }

    @FXML
    private void loadNewTrainingWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/traininginterface.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Neural Network Trainer");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            Calert.showAlert("Cant launch new window", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void batchImageTrainingCompleted() {
        saveButton.setDisable(false);
        spinner.setVisible(false);
    }

    @Override
    public void batchImageTrainingUpdate(int iteration, Double error) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                series.getData().add(new XYChart.Data(String.valueOf(iteration), error));
            }
        });
    }

}
