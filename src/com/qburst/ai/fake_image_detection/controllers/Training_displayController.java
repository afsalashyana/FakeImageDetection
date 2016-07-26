package com.qburst.ai.fake_image_detection.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Training_displayController implements Initializable {

    @FXML
    private AreaChart<Double, Integer> errorChart;
    NumberAxis xAxis,yAxis;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        
    }
    
    public void addData(String error, Integer iter)
    {
//        errorChart.getData().add(new XYChart.Series<String,Integer>(error,iter));
    }

}
