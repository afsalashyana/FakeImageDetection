package com.gc.fakeimagedetection.ui;

import com.gc.fakeimagedetection.ui.alert.CommonUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

public class ELABatchImageProcessor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/batchimageprocessor.fxml"));

        Scene scene = new Scene(root);

        stage.resizableProperty().setValue(false);
        stage.setTitle("Batch ELA processor");
        stage.setScene(scene);
        CommonUtil.attachIcon(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
