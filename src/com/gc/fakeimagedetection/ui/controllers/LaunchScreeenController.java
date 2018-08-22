package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.ui.alert.Calert;
import com.gc.fakeimagedetection.core.metadata.MetadataProcessor;
import com.gc.fakeimagedetection.ui.BatchImageTester;
import com.gc.fakeimagedetection.ui.ELABatchImageProcessor;
import com.gc.fakeimagedetection.ui.SingleImageCheck;
import com.gc.fakeimagedetection.ui.TrainerMain;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    @FXML
    private CheckMenuItem resultPropogation;

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

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.JPG", "*.jpg", "*.jpeg", "*.JPEG");
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
            BorderPane pane = FXMLLoader.load(getClass().getResource("/resources/fxml/launch.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().setAll(pane);
            MetadataProcessor.extracted_data = "";
        } catch (IOException ex) {
            Logger.getLogger(LaunchScreeenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadELAProcessor(ActionEvent event) throws Exception {
        ELABatchImageProcessor processor = new ELABatchImageProcessor();
        Stage stage = new Stage();
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        processor.start(stage);
    }

    @FXML
    private void loadTrainer(ActionEvent event) throws Exception {
        TrainerMain trainer = new TrainerMain();
        Stage stage = new Stage();
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        trainer.start(stage);
        stage.setOnCloseRequest((WindowEvent event1) -> {
            event1.consume();
            stage.close();
        });
    }

    @FXML
    private void loadBatchImageTester(ActionEvent event) throws Exception {
        BatchImageTester tester = new BatchImageTester();
        Stage stage = new Stage();
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        tester.start(stage);
    }

    @FXML
    private void loadSingleImageTester(ActionEvent event) throws Exception {
        SingleImageCheck siCheck = new SingleImageCheck();
        Stage stage = new Stage();
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        siCheck.start(stage);
        stage.setOnCloseRequest((WindowEvent event1) -> {
            event1.consume();
            stage.close();
        });
    }

    @FXML
    private void loadFullScreen(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    @FXML
    private void changeResultPropogation(ActionEvent event) {
        ConstantObjects.shouldPropogateResult = resultPropogation.isSelected();
    }

}
