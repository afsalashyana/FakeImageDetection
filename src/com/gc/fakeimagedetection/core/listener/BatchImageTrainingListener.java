package com.gc.fakeimagedetection.core.listener;

public interface BatchImageTrainingListener {

    public void batchImageTrainingCompleted();

    public void batchImageTrainingUpdate(int iteration, Double error);
}
