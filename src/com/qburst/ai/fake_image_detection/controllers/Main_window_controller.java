package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.ai.fake_image_detection.metadata_extractor.metadata_processor;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class Main_window_controller implements Initializable {

    @FXML
    private JFXButton load_image_button;

    @FXML
    private Text christopher;

    @FXML
    private StackPane rootPane;

    @FXML
    private Text description;

    Boolean isFirstTime = true;

    FileChooser fileChooser;
    int duration = 1500;

    File processingFile = null;

    ScaleTransition bulgingTransition;
    ParallelTransition buttonParallelTransition;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void animate() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), load_image_button);
        TranslateTransition tLogo = new TranslateTransition(Duration.millis(duration), christopher);
        TranslateTransition tDesc = new TranslateTransition(Duration.millis(duration), description);

        ScaleTransition st = new ScaleTransition(Duration.millis(duration), load_image_button);
        st.setToX(3);
        st.setToY(3);

        tt.setByY(-180f);

        tLogo.setToY(50);
        tDesc.setToY(500);
        buttonParallelTransition = new ParallelTransition(load_image_button, st, tt, tLogo, tDesc);

        buttonParallelTransition.play();
        buttonParallelTransition.setOnFinished((e) -> {
            load_image_button.setOpacity(1);
        });

    }

    @FXML
    private void loadAnimation(MouseEvent event) {
        if (isFirstTime) {
            animate();
            isFirstTime = false;
        }
    }

    @FXML
    void loadImageSelector(ActionEvent event) {
        if (processingFile != null) {
            return;
        }
        fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        fileChooser.setTitle("Open Resource File");
        processingFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (processingFile == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("No File Selected");
            alert.setHeaderText("Select an Image :-(");
            alert.setContentText("You haven't selected any images");
            alert.showAndWait();
            return;
        }

        removeBannersandDescs();
        loadMetaDataCheck();
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

    }

    private void removeBannersandDescs() {
        TranslateTransition tChristopher = new TranslateTransition(Duration.millis(duration), christopher);
        TranslateTransition tDescription = new TranslateTransition(Duration.millis(duration), description);
        tChristopher.setToY(-200);
        tDescription.setToY(1000);
        ParallelTransition pt = new ParallelTransition(tChristopher, tDescription);
        pt.play();
    }

    private void loadMetaDataCheck() {
        startSimpleMetaDataAnimation();

        metadata_processor processor = new metadata_processor(processingFile);

        bulgingTransition.setOnFinished((e) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(duration), load_image_button);
            ScaleTransition st = new ScaleTransition(Duration.millis(duration), load_image_button);
            st.setToX(1);
            st.setToY(1);

            tt.setToX(10f);
            tt.setToY(10f);

            ParallelTransition pt = new ParallelTransition(load_image_button, st, tt);
            pt.play();
        });

    }

    private void startSimpleMetaDataAnimation() {
        float animationExtension = 3.25f;
        bulgingTransition = new ScaleTransition(Duration.millis(1000), load_image_button);
        bulgingTransition.setToX(animationExtension);
        bulgingTransition.setToY(animationExtension);
        bulgingTransition.autoReverseProperty().setValue(true);
        bulgingTransition.setCycleCount(2);
        bulgingTransition.play();
        load_image_button.setFont(Font.font("Roboto", FontWeight.NORMAL, 8));
        load_image_button.setText("Checking Metadata..");

    }

}
