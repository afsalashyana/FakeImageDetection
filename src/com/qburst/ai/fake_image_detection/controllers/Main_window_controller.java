package com.qburst.ai.fake_image_detection.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.ai.fake_image_detection.metadata_extractor.metadata_processor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    @FXML
    private AnchorPane anchorPane;

    Boolean isFirstTime = true;

    FileChooser fileChooser;
    int duration = 1500;

    File processingFile = null;

    ScaleTransition bulgingTransition;
    ParallelTransition buttonParallelTransition;
    @FXML
    private ImageView homeIcon;

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
                new File(System.getProperty("user.home") + "/Pictures/Selected")
        );
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilter
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

            tt.setToX(360f);
            tt.setToY(10f);

            ParallelTransition pt = new ParallelTransition(load_image_button, st, tt);
            pt.play();

            pt.setOnFinished((e1) -> {
                loadMetadataResult();
                homeIcon.setVisible(true);
            });

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

    private void loadMetadataResult() {
        try {
            AnchorPane result = FXMLLoader.load(getClass().getResource("/resources/fxml/metadata_result.fxml"));
            anchorPane.getChildren().add(result);
        } catch (IOException ex) {
            Logger.getLogger(Main_window_controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void rollBack(MouseEvent event) {
        try {
            StackPane pane = FXMLLoader.load(getClass().getResource("/resources/fxml/main_window.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            Logger.getLogger(Main_window_controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
