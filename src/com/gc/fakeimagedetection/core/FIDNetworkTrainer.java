package com.gc.fakeimagedetection.core;

import com.gc.fakeimagedetection.core.listener.BatchImageTrainingListener;
import com.gc.fakeimagedetection.core.trainer.BatchImageTrainer;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FIDNetworkTrainer implements BatchImageTrainingListener {

    private final float MERROR = 0.01f;
    private final float LEARNING_RATE = 0.1f;
    private final float MOMENTUM = 0.7f;

    public static void main(String[] args) {
        if (args == null || args.length < 5) {
            System.err.println("usage : <source> <neural path> <width> <height> <labels(comma seperated)>");
            return;
        }

        String sourceDir = args[0];
        String neuralDir = args[1];
        String sWidth = args[2];
        String sHeight = args[3];
        String imageLabelString = args[4];

        new FIDNetworkTrainer().run(sourceDir, neuralDir, sWidth, sHeight, imageLabelString);
    }

    @Override
    public void batchImageTrainingCompleted() {
        System.out.println("Training Completed");
    }

    @Override
    public void batchImageTrainingUpdate(int iteration, Double error) {
        System.out.println("Iteration " + iteration + "\t Error = " + error);
    }

    private void run(String sourceDir, String neuralDir, String sWidth, String sHeight, String imageLabelString) {
        ArrayList<String> labels = new ArrayList<>();
        labels.addAll(Arrays.asList(imageLabelString.split(",")));
        BatchImageTrainer neuralTrainer
                = new BatchImageTrainer(new File(sourceDir), new File(neuralDir),
                        new Dimension(Integer.parseInt(sWidth), Integer.parseInt(sHeight)), labels, this);
        neuralTrainer.setMaxError(MERROR);
        neuralTrainer.setMomentum(MOMENTUM);
        neuralTrainer.setLearningRate(LEARNING_RATE);
        neuralTrainer.start();
    }

}
