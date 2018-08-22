package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.core.metadata.MetadataProcessor;
import com.gc.fakeimagedetection.ui.alert.CommonUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class MetadataResultController implements Initializable {

    @FXML
    private PieChart pie_chart;

    ObservableList<PieChart.Data> pieChartData;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXButton resultButton;

    MetadataDisplayController metadata_display_controller;
    int fakeness = 1;
    int real = 1;
    String output = "";
    String fakeReason = "";
    String realReason = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generatePercentage();
        processResult();

    }

    private void processResult() {
        String extractedData = MetadataProcessor.extracted_data;
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
        } else {
            resultButton.setText("Digitally Altered. Not from Camera");
            resultButton.setStyle("-fx-background-color:#FF5722");
        }

        if (extractedData.contains("Original Transmission Reference") && extractedData.contains("Special Instructions")) {
            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
            snackbar.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());
            snackbar.show("Downloaded Image From Facebook", 10000);
        }
        if (extractedData.contains("Software - Google")) {
            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
            snackbar.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());
            snackbar.show("Tampered By Google. May be from Google+", 10000);
        }

    }

    void displayMetaData() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/metadatadisplay.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            metadata_display_controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.resizableProperty().set(false);
            stage.setTitle("Metadata Information");
            stage.setScene(new Scene(root1));
            CommonUtil.attachIcon(stage);
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
        realReason = "Contains ";
        String extractedData = MetadataProcessor.extracted_data;
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
                realReason += string + ",";
                if (real % 5 == 0) {
                    realReason += "\n";
                }
            }
        }
        realReason = realReason.substring(0, realReason.length() - 1);  //Remove last Comma

        int ctr = 0;
        for (String string : MetadataProcessor.extracted_data.split("\n")) {
            ctr++;
            if (string.toUpperCase().contains("ADOBE")) {
                fakeness += 6;
                if (!fakeReason.contains("Detected Adobe Tag")) {
                    fakeReason += "Detected Adobe Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("PHOTOSHOP")) {
                fakeness += 15;
                if (!fakeReason.contains("Detected Photoshop Tag")) {
                    fakeReason += "Detected Photoshop Tag" + "\n";
                }
            }
            if (string.toUpperCase().contains("GIMP")) {
                fakeness += 15;
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
            if (string.contains("Software - Google")) {
                fakeness += 10;
                if (!fakeReason.contains("Google's Signature Found: Possibly Google+")) {
                    fakeReason += "Google's Signature Found: Possibly Google+" + "\n";
                }
            }
        }

        if (ctr < 15) {
            fakeness += 4;
            fakeReason += "Very Low Metadata Content. Edited" + "\n";
        } else if (ctr < 21) {
            fakeness += 3;
            fakeReason += "Low Metadata Content. Edited" + "\n";
        } else if (ctr < 30) {
            fakeness += 1;
            fakeReason += "Average Metadata Content. Edited" + "\n";
        }
        int total = fakeness + real;

        float fakenessPercentage = (float) fakeness / total;
        float realPercentage = (float) real / total;
        ConstantObjects.fakeness = fakenessPercentage;
        ConstantObjects.realness = realPercentage;
        System.out.println("Fakeness from metedata = " + fakenessPercentage);

        System.err.println("Number of metadata fields = " + ctr);
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList();
        pie_chart.setData(pieChartData);
        pieChartData.add(new PieChart.Data("Fake", fakeness));
        pieChartData.add(new PieChart.Data("Real", real));
        pie_chart.setLegendVisible(false);
        pie_chart.setTitle("");

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
                if (fakeReason.length() > 2) {
                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());
                    snackbar.show(fakeReason, 8000);
                }
                caption.setText(text);
            }
        });

        PieChart.Data data1 = pie_chart.getData().get(1);  //Fake
        data1.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                caption.setTranslateX(e.getSceneX());
                caption.setTranslateY(e.getSceneY());
                String text = String.format("%.1f%%", 100 * data1.getPieValue() / total);
                if (realReason.length() > 10) {
                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());
                    snackbar.show(realReason + " Information", 8000);
                }
                caption.setText(text);
            }
        });

    }

}
