package com.gc.fakeimagedetection.core.trainer;

import com.gc.fakeimagedetection.core.listener.BatchImageTrainingListener;
import com.gc.fakeimagedetection.core.multithread.NotifyingThread;
import com.gc.fakeimagedetection.ui.alert.Calert;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageUtilities;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;

public class BatchImageTrainer extends NotifyingThread implements LearningEventListener {

    File srcDirectory;
    File nnFile;
    Dimension sampleDimension;
    ArrayList<String> imageLabels;
    NeuralNetwork nnet;
    float learningRate = 0f;
    float momentum = 0f;
    float maxError = 0f;
    BatchImageTrainingListener listener;

    public BatchImageTrainer(File srcDirectory,
            File nnFile,
            Dimension sampleDimension,
            ArrayList<String> imageLabels,
            BatchImageTrainingListener listener) {
        this.srcDirectory = srcDirectory;
        this.sampleDimension = sampleDimension;
        this.imageLabels = imageLabels;
        this.listener = listener;
        this.nnFile = nnFile;
    }

    public void setMomentum(float momentum) {
        this.momentum = momentum;
    }

    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    public void setMaxError(float maxError) {
        this.maxError = maxError;
    }

    public void stopLearning() {
        try {
            nnet.stopLearning();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveLearnedNetwork(String path) {
        try {
            nnet.save(path);
            Calert.showAlert("Saved", "Neural network saved" + path, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("Failed");
        }
    }

    @Override
    public void doRun() {
        try {
            System.out.println("Starting training thread....." + sampleDimension.toString() + " and " + imageLabels.toString());

            HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage>();
            for (File file : srcDirectory.listFiles()) {
                imageLabels.add(FilenameUtils.removeExtension(file.getName()));
                if (sampleDimension.getWidth() > 0 && sampleDimension.getHeight() > 0) {
                    Double w = sampleDimension.getWidth();
                    Double h = sampleDimension.getHeight();
                    imagesMap.put(file.getName(), ImageUtilities.resizeImage(ImageUtilities.loadImage(file), w.intValue(), h.intValue()));
                }
            }
            Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages(imagesMap);
            DataSet learningData = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, imageRgbData);

            nnet = NeuralNetwork.load(new FileInputStream(nnFile)); //Load NNetwork
            MomentumBackpropagation mBackpropagation = (MomentumBackpropagation) nnet.getLearningRule();
            mBackpropagation.setLearningRate(learningRate);
            mBackpropagation.setMaxError(maxError);
            mBackpropagation.setMomentum(momentum);

            System.out.println("Network Information\nLabel = " + nnet.getLabel()
                    + "\n Input Neurons = " + nnet.getInputsCount()
                    + "\n Number of layers = " + nnet.getLayersCount()
            );

            mBackpropagation.addListener(this);
            System.out.println("Starting training......");
            nnet.learn(learningData, mBackpropagation);
            //Training Completed
            listener.batchImageTrainingCompleted();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "\n" + ex.getLocalizedMessage());
        }

    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
        listener.batchImageTrainingUpdate(bp.getCurrentIteration(), bp.getTotalNetworkError());
    }

    private void loadGraph() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/training_display.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Graph View");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
