package com.qburst.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.qburst.fakeimagedetection.core.metadata.MetadataProcessor;
import com.qburst.fakeimagedetection.core.processor.NeuralNetProcessor;
import com.qburst.fakeimagedetection.core.trainer.SingleImageTrainer;
import com.qburst.fakeimagedetection.core.errorlevelanalysis.Ela;
import com.qburst.fakeimagedetection.core.multithread.ThreadCompleteListener;
import ij.ImagePlus;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NeuralnetInterfaceController implements Initializable, ThreadCompleteListener {

    public static String imageLocation = "";

    public static String getImageLocation() {
        return imageLocation = LaunchScreeenController.processingFile.getAbsolutePath();
    }

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text description;
    @FXML
    private Text christopher;
    @FXML
    private JFXButton navigation_button;
    @FXML
    private ImageView homeIcon;

    NeuralnetInterfaceController thisObject = this;
    ScaleTransition bulgingTransition;
    Ela elaAnalyzer;
    NeuralNetProcessor nprocessor;
    BufferedImage elaImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startAnimation();
        getImageLocation();

        elaAnalyzer = new Ela(imageLocation, 95);
        elaAnalyzer.setName("elaAnalyzer");
        elaAnalyzer.addListener(this);
        elaAnalyzer.start();

    }

    private void startAnimation() {
        navigation_button.setText("Applying ELA on Image");
        float animationExtension = 1.2f;
        bulgingTransition = new ScaleTransition(Duration.millis(2000), navigation_button);
        bulgingTransition.setToX(animationExtension);
        bulgingTransition.setToY(animationExtension);
        bulgingTransition.autoReverseProperty().setValue(true);
        bulgingTransition.setCycleCount(bulgingTransition.INDEFINITE);
        bulgingTransition.play();
    }

    @FXML
    private void loadAnimation(MouseEvent event) {
    }

    @FXML
    private void rollBack(MouseEvent event) {
        LaunchScreeenController.processingFile = null;
        MetadataProcessor.extracted_data = "";
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene pane = new Scene(FXMLLoader.load(getClass().getResource("/resources/fxml/launch.fxml")));
            stage.setScene(pane);
        } catch (IOException ex) {
            Logger.getLogger(NeuralnetInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void updateIndicatorText(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                navigation_button.setText(message);
            }
        });
    }

    void finalMoveOfIndicator() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Timeline timeline = new Timeline();
                timeline.setCycleCount(1);

                KeyValue keyValueX = new KeyValue(navigation_button.prefWidthProperty(), 300);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(2000), keyValueX);
                timeline.getKeyFrames().add(keyFrame);

                timeline.play();
                navigation_button.setStyle("-fx-background-radius: 0px;");
            }
        });
    }

    void loadResult() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double real = NeuralNetProcessor.real * 100;
                double fake = NeuralNetProcessor.fake * 100;
                DecimalFormat df2 = new DecimalFormat(".#");
                if (real < 10 && fake < 10) {
                    String possibility;
                    if (real >= fake) {
                        possibility = "Possibly Real";
                    } else {
                        possibility = "Possibly Fake";
                    }
                    navigation_button.setStyle("-fx-background-color:#EF6C00");
                    navigation_button.setText("Cant Determine whether fake or not " + possibility);

                } else if (fake > real) {
                    navigation_button.setStyle("-fx-background-color:#f44336");
                    navigation_button.setText("FAKE IMAGE" + "\nConfidence :" + df2.format(fake) + "%");
                } else if (fake < real) {
                    navigation_button.setStyle("-fx-background-color:#4CAF50");
                    navigation_button.setText("REAL IMAGE" + "\nConfidence :" + df2.format(real) + "%");
                } else {
                    navigation_button.setStyle("-fx-background-color:#4CAF50");
                    navigation_button.setText("Process Failed. 50-50 Chance");
                }

                final JFXSnackbar snackbar = new JFXSnackbar(rootPane);
                snackbar.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());
                EventHandler handler = new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        snackbar.unregisterSnackbarContainer(rootPane);
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation Dialog");
                        alert.setContentText("Is this image Fake or Real ?");

                        ButtonType fakeButton = new ButtonType("Fake");
                        ButtonType realButton = new ButtonType("Real");
                        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(fakeButton, realButton, buttonTypeCancel);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == fakeButton) {
                            navigation_button.setText("Learning...");
                            SingleImageTrainer learner
                                    = new SingleImageTrainer(NeuralNetProcessor.nnet, elaImage, false);
                            learner.addListener(thisObject);
                            learner.setName("learner");
                            learner.start();
                        } else if (result.get() == realButton) {
                            navigation_button.setText("Learning...");
                            SingleImageTrainer learner
                                    = new SingleImageTrainer(NeuralNetProcessor.nnet, elaImage, true);
                            learner.addListener(thisObject);
                            learner.setName("learner");
                            learner.start();
                        } else {

                        }
                    }
                };
                snackbar.show("Isn't That Right ? Help Me Grow", "Okay", 10000, handler);

                addELAListener();
            }

            private void addELAListener() {
                navigation_button.setOnAction((e) -> {
                    new ImagePlus("Error Level Analysis", elaImage).show();
                });
            }
        });
    }

    private void removeBannersandDescs() {
        TranslateTransition tChristopher = new TranslateTransition(Duration.millis(1000), christopher);
        TranslateTransition tDescription = new TranslateTransition(Duration.millis(1000), description);
        tChristopher.setToY(-500);
        tDescription.setToY(-500);
        ParallelTransition pt = new ParallelTransition(tChristopher, tDescription);
        pt.play();

    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        switch (thread.getName()) {
            case "elaAnalyzer":
                updateIndicatorText("Serializing Image");
                elaImage = elaAnalyzer.getFilteredImage();
                updateIndicatorText("Connecting to Neural Network");
                nprocessor = new NeuralNetProcessor(elaImage);
                nprocessor.setName("nprocessor");
                nprocessor.addListener(this);
                nprocessor.start();
                removeBannersandDescs();
                break;
            case "nprocessor":
                bulgingTransition.stop();
                updateIndicatorText("Done");
                loadResult();
                break;
            case "learner":
                updateIndicatorText("Learning Complete");
                break;
        }

    }
}
