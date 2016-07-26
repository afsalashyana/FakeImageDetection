package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
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
    private LineChart<?, ?> lineChart;

    File srcDir = null;
    String rLabel = "";
    String fLabel = "";
    
    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void loadImageSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Traing Set");
        File selectedDirectory = chooser.showDialog(rootPane.getScene().getWindow());
        System.out.println("Loading Dataset from " + selectedDirectory.getAbsolutePath());
        srcIndicator.setSelected(true);
    }

    @FXML
    private void startTraining(ActionEvent event) {
        rLabel = realLabel.getText();
        fLabel = fakeLabel.getText();
        System.out.println("Starting training procedure");
    }

}
