package com.qburst.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.qburst.fakeimagedetection.ui.alert.Calert;
import com.qburst.fakeimagedetection.core.metadata.MetadataProcessor;
import com.qburst.fakeimagedetection.core.constants.ConstantObjects;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class LaunchScreeenController implements Initializable {

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

    @FXML
    private ImageView homeIcon;
    @FXML
    private ImageView backgroundImageView;

    int duration = 1500;
    FileChooser fileChooser;
    Boolean isFirstTime = true;
    ScaleTransition bulgingTransition;
    ParallelTransition buttonParallelTransition;
    public static File processingFile = null;
    public static StackPane parentPaneForAll;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        parentPaneForAll = rootPane;

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
            Calert.showAlert("No File Selected", "You haven't selected any images", AlertType.ERROR);
            return;
        }

        try {
            backgroundImageView.setImage(new Image(new FileInputStream(processingFile)));
            backgroundImageView.setOpacity(0.5);
        } catch (Exception ex) {
            Logger.getLogger(LaunchScreeenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        removeBannersandDescs();
        loadMetaDataCheck();
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Pictures")
        );
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("Image Files", "*.JPG","*.jpg","*.png","*.PNG","*.jpeg","*.JPEG","*.TIFF","*.TIF");
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

        MetadataProcessor processor = new MetadataProcessor(processingFile);

        bulgingTransition.setOnFinished((e) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(duration - 500), load_image_button);
            ScaleTransition st = new ScaleTransition(Duration.millis(duration - 500), load_image_button);
            st.setToX(1);
            st.setToY(1);

            tt.setToX(-150f);
            tt.setToY(80f);

            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);

            KeyValue keyValueX = new KeyValue(load_image_button.prefWidthProperty(), 400);
            KeyValue keyValueY = new KeyValue(load_image_button.prefHeightProperty(), 50);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(duration - 500), keyValueX, keyValueY);
            timeline.getKeyFrames().add(keyFrame);

            ParallelTransition pt = new ParallelTransition(load_image_button, st, tt, timeline);
            pt.play();

            pt.setOnFinished((e1) -> {
                loadMetadataResult();
                load_image_button.setText("Test On AI");
                load_image_button.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
                homeIcon.setVisible(true);
//                Neural Network Entry
                load_image_button.setOnMouseClicked((e2) -> {
                    System.out.println("Loading NN........");
                    try {
                        anchorPane.getChildren().clear();
                        StackPane pane = FXMLLoader.load(getClass().getResource("/resources/fxml/neuralinterface.fxml"));
                        anchorPane.getChildren().setAll(pane);
                    } catch (IOException ex) {
                        Logger.getLogger(MetadataResultController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
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
            AnchorPane result = FXMLLoader.load(getClass().getResource("/resources/fxml/metadataresult.fxml"));
            anchorPane.getChildren().add(result);
        } catch (IOException ex) {
            Logger.getLogger(LaunchScreeenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void rollBack(MouseEvent event) {
        processingFile = null;
        try {
            StackPane pane = FXMLLoader.load(getClass().getResource("/resources/fxml/launch.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().setAll(pane);
            MetadataProcessor.extracted_data = "";
        } catch (IOException ex) {
            Logger.getLogger(LaunchScreeenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
