package com.gc.fakeimagedetection.core.listener;

import java.util.HashMap;

public interface NeuralnetProcessorListener {

    public void neuralnetProcessCompleted(HashMap<String, Double> result);
}
