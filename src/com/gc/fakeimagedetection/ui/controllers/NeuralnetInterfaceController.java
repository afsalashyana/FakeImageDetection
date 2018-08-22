package com.gc.fakeimagedetection.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.core.errorlevelanalysis.FIDErrorLevelAnalysis;
import com.gc.fakeimagedetection.core.listener.ErrorLevelAnalysisListener;
import com.gc.fakeimagedetection.core.listener.NeuralnetProcessorListener;
import com.gc.fakeimagedetection.core.metadata.MetadataProcessor;
import com.gc.fakeimagedetection.core.processor.NeuralNetProcessor;
import com.gc.fakeimagedetection.core.trainer.SingleImageTrainer;
import com.gc.fakeimagedetection.core.listener.ThreadCompleteListener;
import com.gc.fakeimagedetection.ui.alert.Calert;
import ij.ImagePlus;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

public class NeuralnetInterfaceController implements
        Initializable, ErrorLevelAnalysisListener, ThreadCompleteListener, NeuralnetProcessorListener {

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
    FIDErrorLevelAnalysis elaAnalyzer;
    NeuralNetProcessor nprocessor;
    BufferedImage elaImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startAnimation();
        getImageLocation();

        elaAnalyzer = new FIDErrorLevelAnalysis(imageLocation, 95, new Dimension(100, 100), this);
        elaAnalyzer.setName("elaAnalyzer");
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

    void loadResult(HashMap<String, Double> result) {
        System.out.println("Metadata Result: Fakeness = " + ConstantObjects.fakeness);
        if (ConstantObjects.fakeness < 0) {
            System.out.println("Invalid Result from metadata");
        }

        Platform.runLater(new Runnable() {
            final float NEURAL_NET_WEIGHT = 0.4f;
            final float METADATA_NET_WEIGHT = 1-NEURAL_NET_WEIGHT;

            @Override
            public void run() {
                double real = result.get("real") * 100;
                double fake = result.get("faked") * 100;

                DecimalFormat df2 = new DecimalFormat(".#");
                if (ConstantObjects.shouldPropogateResult) {
                    real = (real*NEURAL_NET_WEIGHT) + (METADATA_NET_WEIGHT * ConstantObjects.realness*100);
                    fake = (fake*NEURAL_NET_WEIGHT) + (METADATA_NET_WEIGHT * ConstantObjects.fakeness*100);
                }

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
                });

                navigation_button.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            new ImagePlus("Error Level Analysis", elaImage).show();
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            try {
                                new ImagePlus("Original Image", ImageIO.read(LaunchScreeenController.processingFile)).show();
                            } catch (IOException ex) {
                                Logger.getLogger(NeuralnetInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
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
            case "learner":
                updateIndicatorText("Learning Complete");
                break;
        }
    }

    @Override
    public void elaCompleted(BufferedImage image) {
        updateIndicatorText("Serializing Image");
        elaImage = image;
        updateIndicatorText("Connecting to Neural Network");
        nprocessor = new NeuralNetProcessor(elaImage);
        nprocessor.setName("nprocessor");
        nprocessor.addListener(this);
        nprocessor.setListener(this);
        nprocessor.start();
        removeBannersandDescs();
    }

    @Override
    public void neuralnetProcessCompleted(HashMap<String, Double> result) {
        if (result == null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Calert.showAlert("Forced Rollback", "Image Detection Failed:\nCorrupted File", AlertType.ERROR);
                    rollBack(null);
                    return;
                }
            });
        }
        bulgingTransition.stop();
        updateIndicatorText("Done");
        System.out.println("Neural net result:-\n" + result);
        loadResult(result);
    }
}
