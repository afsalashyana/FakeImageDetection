package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXListView;
import com.qburst.ai.fake_image_detection.metadata_extractor.metadata_processor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

public class Metadata_result_controller implements Initializable {

    @FXML
    private PieChart pie_chart;
    @FXML
    private JFXListView<String> meta_data_list_view;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inflateListView();
    }    
    
    public void inflateListView()
    {
        for (String string : metadata_processor.extracted_data.split("\n")) {
            meta_data_list_view.getItems().add(string);
            System.out.println("Adding Items");
        }
    }
    
}
