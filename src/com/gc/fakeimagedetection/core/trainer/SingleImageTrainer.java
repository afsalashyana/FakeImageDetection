package com.gc.fakeimagedetection.core.trainer;

import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.core.processor.NeuralNetProcessor;
import com.gc.fakeimagedetection.core.multithread.NotifyingThread;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageUtilities;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;

public class SingleImageTrainer extends NotifyingThread implements LearningEventListener {

    NeuralNetwork nnet;
    BufferedImage image;
    Boolean isReal;
    ArrayList<String> labels;
    float learningRate = 0.2f;
    float maxError = 0.01f;
    float momentum = 0.6f;
    public static Boolean isDirty = false;

    public SingleImageTrainer(NeuralNetwork nnet, BufferedImage image, Boolean isReal) {
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
        if (!isReal) {
            fileName = "real";
        } else {
            fileName = "faked";
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

        //Mark nnet as dirty. Write on close
        isDirty = true;
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
    }

}
