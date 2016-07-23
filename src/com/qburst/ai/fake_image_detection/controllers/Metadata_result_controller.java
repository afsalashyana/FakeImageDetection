package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.ai.fake_image_detection.metadata_extractor.metadata_processor;
import com.qburst.ai.fake_image_detection.ui.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Metadata_result_controller implements Initializable {

    @FXML
    private PieChart pie_chart;

    ObservableList<PieChart.Data> pieChartData;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXButton resultButton;

    int fakeness = 1;
    int real = 1;
    String output = "";
    String fakeReason = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generatePercentage();
        processResult();

    }

    private void processResult() {
        String extractedData = metadata_processor.extracted_data;
        ArrayList<String> components = new ArrayList<>();
        for (String component : extractedData.split("\n")) {
            components.add(component);
        }

        if (extractedData.contains("Photoshop") || extractedData.contains("PHOTOSHOP")) {
            String version = "";
            for (String component : components) {
                if (component.contains("Adobe Photoshop")) {
                    try {
                        version = component.substring(component.indexOf('A'));
                    } catch (Exception e) {
                    }
                }
            }
            if (version.length() < 2) {
                resultButton.setText("Fake Image With Adobe Tag\n");
            } else {
                resultButton.setText("Tampered With\n" + version.replaceAll("[(,)]", ""));
            }
            resultButton.setStyle("-fx-background-color:#ff0000");
        } else if (extractedData.contains("Gimp") || extractedData.contains("GIMP")) {
            resultButton.setText("Tampered With Gimp");
            resultButton.setStyle("-fx-background-color:#ff0000");
        } else if (real > fakeness) {
            if (fakeness < 3) {
                resultButton.setText("Seems like Real Camera Image");
                resultButton.setStyle("-fx-background-color:#64DD17");
            } else {
                resultButton.setText("Metadata Is Clean");
                resultButton.setStyle("-fx-background-color:#64DD17");
            }
        } else if (real == fakeness) {
            resultButton.setText("Cant Determine. Proceed with AI");
            resultButton.setStyle("-fx-background-color:#455A64");
        }
        else
        {
            resultButton.setText("Digitally Altered. Not from Camera");
            resultButton.setStyle("-fx-background-color:#FF5722");
        }

    }

    void displayMetaData() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/metadata_display.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Metadata Information");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {

        }
    }

    @FXML
    private void displayMoreInfo(ActionEvent event) {
        displayMetaData();
    }

    private void generatePercentage() {
        real = 1;
        fakeness = 1;
        String extractedData = metadata_processor.extracted_data;
        ArrayList<String> componentsToCheck = new ArrayList<>();
        componentsToCheck.add("Exif IFD0");
        componentsToCheck.add("Exif SubIFD");
        componentsToCheck.add("Interoperability");
        componentsToCheck.add("Exif Thumbnail");
        componentsToCheck.add("Model");
        componentsToCheck.add("Make");
        componentsToCheck.add("Exposure Time ");
        componentsToCheck.add("F-Number");
        componentsToCheck.add("Flash");
        componentsToCheck.add("Focal Length");
        for (String string : componentsToCheck) {
            if (extractedData.contains(string)) {
                real++;
            }
        }
        int ctr = 0;
        for (String string : metadata_processor.extracted_data.split("\n")) {
            ctr++;
            if (string.toUpperCase().contains("ADOBE")) {
                fakeness += 3;
                if (!fakeReason.contains("Detected Adobe Tag")) {
                    fakeReason += "Detected Adobe Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("PHOTOSHOP")) {
                fakeness += 10;
                if (!fakeReason.contains("Detected Photoshop Tag")) {
                    fakeReason += "Detected Photoshop Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("GIMP")) {
                fakeness += 10;
                if (!fakeReason.contains("Detected Gimp Tag")) {
                    fakeReason += "Detected Gimp Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("COREL")) {
                fakeness += 10;
                if (!fakeReason.contains("Detected Corel Tag")) {
                    fakeReason += "Detected Corel Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("PAINT")) {
                fakeness += 10;
                if (!fakeReason.contains("Detected Paint Tag")) {
                    fakeReason += "Detected Paint Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("PIXLR")) {
                fakeness += 10;
                if (!fakeReason.contains("Detected Pixlr Tag")) {
                    fakeReason += "Detected Pixlr Tag" + "\n";
                }
            }
        }

        if (ctr < 15) {
            fakeness += 4;
            fakeReason += "Very Low Metadata Content" + "\n";
        } else if (ctr < 21) {
            fakeness += 3;
            fakeReason += "Low Metadata Content" + "\n";
        } else if (ctr < 30) {
            fakeness += 1;
            fakeReason += "Average Metadata Content" + "\n";
        }
        int total = fakeness + real;

        System.err.println("Number of metadata fields = " + ctr);
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList();
        pie_chart.setData(pieChartData);
        pieChartData.add(new PieChart.Data("Fake", fakeness));
        pieChartData.add(new PieChart.Data("Real", real));

        final Label caption = new Label("");
        caption.setTextFill(Color.WHITE);
        caption.setStyle("-fx-font: 24 arial;");
        final ObservableList<Node> children = anchorPane.getChildren();
        children.add(caption);

        PieChart.Data data = pie_chart.getData().get(0);  //Fake
        data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                caption.setTranslateX(e.getSceneX());
                caption.setTranslateY(e.getSceneY());
                String text = String.format("%.1f%%", 100 * data.getPieValue() / total);
                Toast.makeText((Stage) anchorPane.getScene().getWindow(), fakeReason, 5000, 500, 500);
                caption.setText(text);
            }
        });

    }

}
