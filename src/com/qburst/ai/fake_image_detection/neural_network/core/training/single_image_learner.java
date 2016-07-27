package com.qburst.ai.fake_image_detection.neural_network.core.training;

import com.qburst.ai.fake_image_detection.common.cAlert;
import com.qburst.ai.fake_image_detection.neural_network.core.neural_net_processor;
import com.qburst.ai.fake_image_detection.neural_network.thread_sync.NotifyingThread;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageUtilities;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;

public class single_image_learner extends NotifyingThread implements LearningEventListener {

    NeuralNetwork nnet;
    BufferedImage image;
    Boolean isReal;
    ArrayList<String> labels;
    float learningRate = 0.2f;
    float maxError = 0.01f;
    float momentum = 0.6f;

    public single_image_learner(NeuralNetwork nnet, BufferedImage image, Boolean isReal) {
        this.nnet = nnet;
        this.image = image;
        this.isReal = isReal;
        labels = new ArrayList<>();
        labels.add("real");
        labels.add("faked");
    }

    @Override
    public void doRun() {
        HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage>();
        String fileName = "";
        if (isReal) {
            fileName = "real_1";
        } else {
            fileName = "faked_1";
        }

        System.out.println("Teaching as " + fileName);
        imagesMap.put(fileName, image);
        Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages(imagesMap);
        DataSet learningData = ImageRecognitionHelper.createRGBTrainingSet(labels, imageRgbData);
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

        //Save Updated NN
        try {
            nnet.save(neural_net_processor.nNetworkpath);
        } catch (Exception e) {
            cAlert.showAlert("Cant save updated network", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
    }

}
