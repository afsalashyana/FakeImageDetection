package com.gc.fakeimagedetection.ui;

import com.gc.fakeimagedetection.ui.alert.CommonUtil;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static javafx.application.Application.launch;

public class SingleImageCheck extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/singleimage.fxml"));

        Scene scene = new Scene(root);

        stage.resizableProperty().setValue(false);
        stage.setTitle("Single Image Checker");
        stage.setScene(scene);
        stage.show();
        CommonUtil.attachIcon(stage);

        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
