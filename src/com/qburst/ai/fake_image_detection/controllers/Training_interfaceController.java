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
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageLabels = new ArrayList<>();
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
        rLabel = realLabel.getText();
        fLabel = fakeLabel.getText();
        imageLabels.add(rLabel);
        imageLabels.add(fLabel);
        int sampledWidth = Integer.parseInt(width.getText());
        int sampledheight = Integer.parseInt(height.getText());
        int lRate = Integer.parseInt(learningRate.getText());
        int moment = Integer.parseInt(momentum.getText());
        int mError = Integer.parseInt(maxError.getText());
        
        System.out.println("Starting training procedure");
        Nn_trainer neuralTrainer = new Nn_trainer(srcDir,
                new Dimension(sampledWidth, sampledWidth), imageLabels);
        neuralTrainer.setMaxError(mError);
        neuralTrainer.setMomentum(moment);
        neuralTrainer.setLearningRate(lRate);
        neuralTrainer.start();
    }

}
