package com.gc.fakeimagedetection.ui;

import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.core.processor.NeuralNetProcessor;
import com.gc.fakeimagedetection.core.trainer.SingleImageTrainer;
import com.gc.fakeimagedetection.ui.alert.CommonUtil;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/launch.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Christopher");
        stage.setScene(scene);
        stage.show();
        CommonUtil.attachIcon(stage);
        stage.setOnCloseRequest((WindowEvent event) -> {
            if (SingleImageTrainer.isDirty) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save Changes ?");
                alert.setHeaderText("Neural Network has been updated with new data");
                alert.setContentText("Do you want to save changes ?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    NeuralNetProcessor.nnet.save(ConstantObjects.neuralNetworkPath);
                    System.out.println("Saved to " + ConstantObjects.neuralNetworkPath);
                    System.exit(0);
                } else {
                    System.out.println("Changes discared");
                    System.exit(1);
                }        
            }
        });
    }

    public static void main(String[]  args) {
        launch(args);
    }

}
