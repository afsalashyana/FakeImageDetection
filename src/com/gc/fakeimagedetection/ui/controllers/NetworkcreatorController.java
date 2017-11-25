package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.gc.fakeimagedetection.core.listener.NeuralNetworkCreationCompleteListener;
import com.gc.fakeimagedetection.core.neuralnet.MLPNetworkMaker;
import com.gc.fakeimagedetection.ui.alert.Calert;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.util.TransferFunctionType;

public class NetworkcreatorController implements Initializable, NeuralNetworkCreationCompleteListener {

    @FXML
    private JFXTextField neuralNetLabel;
    @FXML
    private JFXTextField width;
    @FXML
    private JFXTextField height;
    @FXML
    private JFXComboBox<String> colorMode;
    @FXML
    private JFXTextField neuronLabelList;
    @FXML
    private JFXTextField neuronCountList;
    @FXML
    private JFXComboBox<String> transferFunction;
    @FXML
    private Pane container;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXSpinner loadingSpinner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadingSpinner.setVisible(false);

        //Add ColorModes ,        
        ObservableList list = FXCollections.observableArrayList();
        list.add("COLOR_RGB");
        list.add("COLOR_HSL");
        list.add("BLACK_AND_WHITE");
        colorMode.getItems().setAll(list);

        //Add TransferFunctionss
        ObservableList listTransferFunctions = FXCollections.observableArrayList();
        listTransferFunctions.add("LINEAR");
        listTransferFunctions.add("RAMP");
        listTransferFunctions.add("STEP");
        listTransferFunctions.add("SIGMOID");
        listTransferFunctions.add("TANH");
        listTransferFunctions.add("GAUSSIAN");
        listTransferFunctions.add("TRAPEZOID");
        listTransferFunctions.add("SGN");
        listTransferFunctions.add("SIN");
        listTransferFunctions.add("LOG");
        transferFunction.getItems().setAll(listTransferFunctions);

    }

    @FXML
    private void saveNeuralNet(ActionEvent event) {
        String neuralNetLbl = neuralNetLabel.getText();
        Dimension samplingDimension = new Dimension(Integer.parseInt(width.getText()),
                Integer.parseInt(height.getText()));
        ColorMode mode;
        switch (colorMode.getSelectionModel().getSelectedItem()) {
            case "COLOR_RGB":
                mode = ColorMode.COLOR_RGB;
                break;
            case "COLOR_HSL":
                mode = ColorMode.COLOR_HSL;
                break;
            case "BLACK_AND_WHITE":
                mode = ColorMode.COLOR_RGB;
                break;
            default:
                mode = ColorMode.COLOR_RGB;
                break;
        }
        TransferFunctionType tFunction;
        switch (transferFunction.getSelectionModel().getSelectedItem()) {
            case "LINEAR":
                tFunction = TransferFunctionType.LINEAR;
                break;
            case "RAMP":
                tFunction = TransferFunctionType.RAMP;
                break;
            case "STEP":
                tFunction = TransferFunctionType.STEP;
                break;
            case "SIGMOID":
                tFunction = TransferFunctionType.SIGMOID;
                break;
            case "TANH":
                tFunction = TransferFunctionType.TANH;
                break;
            case "GAUSSIAN":
                tFunction = TransferFunctionType.GAUSSIAN;
                break;
            case "TRAPEZOID":
                tFunction = TransferFunctionType.TRAPEZOID;
                break;
            case "SGN":
                tFunction = TransferFunctionType.SGN;
                break;
            case "SIN":
                tFunction = TransferFunctionType.SIN;
                break;
            case "LOG":
                tFunction = TransferFunctionType.LOG;
                break;
            default:
                tFunction = TransferFunctionType.GAUSSIAN;
                break;
        }

        ArrayList<String> neuronLabels = new ArrayList(Arrays.asList(neuronLabelList.getText().split("[,]")));
        ArrayList<Integer> neuronCounts = new ArrayList();

        for (String neuronCount : neuronCountList.getText().split("[,]")) {
            neuronCounts.add(Integer.parseInt(neuronCount.replaceAll(" ", "")));
            System.out.println("neuronCounts = " + neuronCount);
        }

        //Show File save dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Neural Network");
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file == null) {
            Calert.showAlert("Not a valid File", "Select target again", Alert.AlertType.ERROR);
            return;
        }
        MLPNetworkMaker maker = new MLPNetworkMaker(neuralNetLbl, samplingDimension, mode, neuronLabels, neuronCounts, tFunction, file.getAbsolutePath());
        maker.setListener(this);
        Thread nnetCreator = new Thread(maker);
        nnetCreator.start();
        loadingSpinner.setVisible(true);
    }

    @Override
    public void networkCreationComplete(Boolean flag) {
        loadingSpinner.setVisible(false);
        if (flag) {
            Calert.showAlert("Done", "Neural Network Saved Successfully", Alert.AlertType.INFORMATION);
        }
    }

}
