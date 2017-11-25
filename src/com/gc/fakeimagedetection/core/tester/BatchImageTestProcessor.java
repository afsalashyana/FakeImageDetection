package com.gc.fakeimagedetection.core.tester;

import com.gc.fakeimagedetection.core.listener.BatchImageTestingListener;
import com.gc.fakeimagedetection.ui.alert.Calert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import javafx.scene.control.Alert;
import javax.imageio.ImageIO;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;

public class BatchImageTestProcessor implements Runnable {

    private int counter = 0;
    private int fullSuccess = 0;
    private int halfSuccess = 0;
    private int error = 0;
    private double meanSquareError = 0;
    private final File nueralNetwork, realDir, fakeDir;
    BatchImageTestingListener listener;

    public BatchImageTestProcessor(File nueralNetwork, File realDir, File fakeDir) {
        this.nueralNetwork = nueralNetwork;
        this.realDir = realDir;
        this.fakeDir = fakeDir;
    }

    public void setListener(BatchImageTestingListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        if (realDir == null || nueralNetwork == null || fakeDir == null) {
            Calert.showAlert("Invalid Data", "Select Required Files", Alert.AlertType.ERROR);
            return;
        }

        //Load Neural Network   
        NeuralNetwork network = null;
        try {
            network = NeuralNetwork.load(new FileInputStream(nueralNetwork)); // load trained neural network saved with Neuroph Studio
        } catch (FileNotFoundException ex) {
            System.err.println("Neural network failed to load");
            return;
        }
        System.out.println("Learning Rule = " + network.getLearningRule());
        ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) network.getPlugin(ImageRecognitionPlugin.class); // get the 

        //Test Fake Images
        for (File fakeImage : fakeDir.listFiles()) {
            HashMap<String, Double> output;
            try {
                output = imageRecognition.recognizeImage(ImageIO.read(fakeImage));
            } catch (Exception ex) {
                System.err.println("Image Failed to load" + fakeImage.getAbsolutePath() + ex.getMessage());
                continue;
            }
            if (output == null) {
                System.err.println("Image Recognition Failed");
                continue;
            }
            counter++;
            double real = output.get("real");
            double fake = output.get("faked");
            if (fake >= 0.9 && real < 0.1) {
                fullSuccess++;
                System.out.println("Required Out : Fake = 1, Real = 0 :: Output Fake = " + fake + " | Real = " + real);
            } else if (fake > 0.7) {
                halfSuccess++;
                System.out.println("Required Out : Fake = 1, Real = 0 :: Output Fake = " + fake + " | Real = " + real);
            } else {
                System.err.println("Required Out : Fake = 1, Real = 0 :: Output Fake = " + fake + " | Real = " + real + fakeImage.getName());
            }
            if (fake < 0.8) {
                error++;
            }
            meanSquareError += ((1 - fake) * (1 - fake));
        }

        //Test Real Images
        for (File realImage : realDir.listFiles()) {
            HashMap<String, Double> output;
            try {
                output = imageRecognition.recognizeImage(ImageIO.read(realImage));
            } catch (Exception ex) {
                System.err.println("Image Failed to Load " + realImage.getAbsolutePath() + ex.getLocalizedMessage());
                continue;
            }
            if (output == null) {
                System.err.println("Image Recognition Failed");
                continue;
            }
            counter++;
            double real = output.get("real");
            double fake = output.get("faked");
            if (real >= 0.9 && fake < 0.1) {
                fullSuccess++;
                System.out.println("Required Out : Fake = 0, Real = 1 :: Output Fake = " + fake + " | Real = " + real);
            } else if (real > 0.8) {
                halfSuccess++;
                System.out.println("Required Out : Fake = 0, Real = 1 :: Output Fake = " + fake + " | Real = " + real);
            } else {
                System.err.println("Required Out : Fake = 0, Real = 1 :: Output Fake = " + fake + " | Real = " + real + realImage.getName());
            }
            if (real < 0.8) {
                error++;
            }
            meanSquareError += ((1 - real) * (1 - real));
        }
        meanSquareError = meanSquareError / counter;

        String result = "";
        result += ("\n-------------------------------------------------------------------");
        result += ("\nNumber of image processed = " + counter);
        result += ("\n100% Correct Detection = " + fullSuccess);
        result += ("\nAmbigious Correct Detection = " + halfSuccess);
        result += ("\nTotal Error = " + error);
        result += ("\nMean Square Error = " + meanSquareError);
        result += ("\n-------------------------------------------------------------------");
        System.out.println(result);
        listener.testingComplete(result);
    }
}
