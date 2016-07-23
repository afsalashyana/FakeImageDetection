package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Metadata_result_controller implements Initializable {

    @FXML
    private PieChart pie_chart;

    ObservableList<PieChart.Data> pieChartData;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXButton resultButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList();
        pie_chart.setData(pieChartData);
        pieChartData.add(new PieChart.Data("Fake", 13));
        pieChartData.add(new PieChart.Data("Real", 35));

        final Label caption = new Label("");
        caption.setTextFill(Color.WHITE);
        caption.setStyle("-fx-font: 32 Roboto;");

        for (final PieChart.Data data : pieChartData) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        double total = 0;
                        for (PieChart.Data d : pieChartData) {
                            total += d.getPieValue();
                        }
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        String text = String.format("%.1f%%", 100 * data.getPieValue() / total);
                        caption.setText(text);
                    }
            );
        }
    }

    public void inflatePieChart() {

    }

}
