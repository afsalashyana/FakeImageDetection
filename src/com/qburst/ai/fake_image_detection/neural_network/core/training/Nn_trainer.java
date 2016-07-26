package com.qburst.ai.fake_image_detection.neural_network.core.training;

import com.qburst.ai.fake_image_detection.neural_network.thread_sync.NotifyingThread;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class Nn_trainer extends NotifyingThread implements LearningEventListener {

    File srcDirectory;
    Dimension sampleDimension;
    ArrayList<String> imageLabels;
    NeuralNetwork nnet;
    float learningRate = 0f;
    float momentum = 0f;
    float maxError = 0f;
    training_display display;
    

    public Nn_trainer(File srcDirectory, Dimension sampleDimension, ArrayList<String> imageLabels) {
        this.srcDirectory = srcDirectory;
        this.sampleDimension = sampleDimension;
        this.imageLabels = imageLabels;
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
            File NNetwork = new File("nnet/CNN2.nnet");
            if (!NNetwork.exists()) {
                System.out.println("Missing NN");
                return;
            }
            nnet = NeuralNetwork.load(new FileInputStream(NNetwork)); //Load NNetwork
            MomentumBackpropagation mBackpropagation = (MomentumBackpropagation) nnet.getLearningRule();
            mBackpropagation.setLearningRate(learningRate);
            mBackpropagation.setMaxError(maxError);
            mBackpropagation.setMomentum(momentum);

            mBackpropagation.addListener(this);
            System.out.println("Starting training......");
            nnet.learn(learningData, mBackpropagation);
            display = new training_display();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "\n" + ex.getLocalizedMessage());
        }
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
        training_display.addData(bp.getCurrentIteration(), bp.getTotalNetworkError());
    }

}
