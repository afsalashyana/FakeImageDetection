package com.gc.fakeimagedetection.core.neuralnet;

import com.gc.fakeimagedetection.core.listener.NeuralNetworkCreationCompleteListener;
import java.util.List;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.util.TransferFunctionType;

public class MLPNetworkMaker implements Runnable {

    String networkLabel;
    Dimension samplingDimension;
    ColorMode mode;
    List<String> outputNeuronLabels;
    List<Integer> neuronCounts;
    TransferFunctionType type;
    NeuralNetwork nnet;
    String saveLocation;
    NeuralNetworkCreationCompleteListener listener;

    public void setListener(NeuralNetworkCreationCompleteListener listener) {
        this.listener = listener;
    }

    public MLPNetworkMaker(String networkLabel, Dimension samplingDimension, ColorMode mode, List<String> outputNeuronLabels, List<Integer> neuronCounts, TransferFunctionType type, String saveLocation) {
        this.networkLabel = networkLabel;
        this.samplingDimension = samplingDimension;
        this.mode = mode;
        this.outputNeuronLabels = outputNeuronLabels;
        this.neuronCounts = neuronCounts;
        this.type = type;
        this.saveLocation = saveLocation;
    }

    @Override
    public void run() {
        Boolean flag = true;
        try {
            nnet = ImageRecognitionHelper.createNewNeuralNetwork(networkLabel, samplingDimension, mode, outputNeuronLabels, neuronCounts, type);
            if (nnet == null) {
                throw new Exception("Network Creation Failed");
            }
            nnet.save(saveLocation);
        } catch (Exception e) {
            flag = false;
        }
        listener.networkCreationComplete(flag);
    }

}
