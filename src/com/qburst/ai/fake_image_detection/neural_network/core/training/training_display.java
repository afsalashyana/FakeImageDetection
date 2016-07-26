package com.qburst.ai.fake_image_detection.neural_network.core.training;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class training_display extends Application {

    public static XYChart.Series<Number, Number> series;

    @Override
    public void start(Stage stage) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
                xAxis, yAxis);
        lineChart.setAnimated(true);
        lineChart.setTitle("Line Chart");
        series = new XYChart.Series<Number, Number>();
        series.setName("My Data");

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    public static void addData(int iteration, Double error) {
        series.getData().add(new XYChart.Data<Number, Number>(iteration, error));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
