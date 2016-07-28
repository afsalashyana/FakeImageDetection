package com.qburst.fakeimagedetection.core.listener;

import java.util.HashMap;

public interface NeuralnetProcessorListener {

    public void neuralnetProcessCompleted(HashMap<String, Double> result);
}
