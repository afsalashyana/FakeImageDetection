package com.gc.fakeimagedetection.core;

import com.gc.fakeimagedetection.core.errorlevelanalysis.FIDErrorLevelAnalysis;
import com.gc.fakeimagedetection.core.listener.ErrorLevelAnalysisListener;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;

public class FIDNetworkAnalyser implements ErrorLevelAnalysisListener {

    private NeuralNetwork nnet;

    public FIDNetworkAnalyser(String nSourceFile) throws FileNotFoundException {
        nnet = NeuralNetwork.load(new FileInputStream(nSourceFile)); // load trained neural network saved with Neuroph Studio
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args == null || args.length < 5) {
            System.err.println("Usage : <neural net file> <image file> <quality> <width> <height>");
        }
        String neuralSource = args[0];
        String imageLoc = args[1];
        String quality = args[2];
        String sWidth = args[3];
        String sHeight = args[4];

        new FIDNetworkAnalyser(neuralSource)
                .run(imageLoc, Integer.parseInt(quality),
                        new Dimension(Integer.parseInt(sWidth), Integer.parseInt(sHeight)));

    }

    private void run(String imageLocation, int quality, Dimension dim) {
        FIDErrorLevelAnalysis elaAnalyzer = new FIDErrorLevelAnalysis(imageLocation, quality, dim, this);
        elaAnalyzer.start();
    }

    public void processOnNeuralNetwork(BufferedImage elaImage) {
        ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) nnet.getPlugin(ImageRecognitionPlugin.class); // get the image recognition plugin from neural network
        HashMap<String, Double> output = imageRecognition.recognizeImage(elaImage);
        if (output == null) {
            System.err.println("Image Recognition Failed");
        }
        System.out.println(output.toString());
    }

    @Override
    public void elaCompleted(BufferedImage image) {
        if(image==null)
            return;
        processOnNeuralNetwork(image);
    }
}
