package com.gc.fakeimagedetection.core.errorlevelanalysis;

import com.gc.fakeimagedetection.core.listener.ErrorLevelAnalysisUpdateListener;
import com.gc.fakeimagedetection.core.constants.ConstantObjects;
import com.gc.fakeimagedetection.core.listener.ThreadCompleteListener;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public final class ImageStandardizer implements ThreadCompleteListener {

    File sourceDir = null;
    File destDir = null;
    Dimension resolution = null;
    File[] availableFiles = null;
    ArrayList<String> supportedExtensions;
    ErrorLevelAnalysisUpdateListener listener;
    String outputLabel;

    public ImageStandardizer(File sourceDirectory, File destinationDirectory, Dimension finalResolution, String outLabel) {
        System.out.println("Starting new Image Standardizer");
        sourceDir = sourceDirectory;
        resolution = finalResolution;
        destDir = destinationDirectory;
        outputLabel = outLabel;
    }

    public void run() {
        init();
        loadDirectory(sourceDir.getAbsolutePath());
        processImages(outputLabel);

    }

    public void setListener(ErrorLevelAnalysisUpdateListener listener) {
        this.listener = listener;
    }

    void init() {
        supportedExtensions = new ArrayList<>(Arrays.asList(ConstantObjects.supportedExtensions));
    }

    void processImages(String outLabel) {
        System.out.println("Calling Processor with dimension " + resolution);
        ErrorLevelAnalyzer imageProcessor
                = new ErrorLevelAnalyzer(sourceDir.getAbsolutePath(), destDir.getAbsolutePath(), 95, supportedExtensions, resolution, outLabel);
        imageProcessor.setListener(listener);
        imageProcessor.addListener(this);
        imageProcessor.start();
    }

    public void loadDirectory(String path) {
        System.out.println("Loading directory....");
        sourceDir = new File(path);
        if (!sourceDir.exists() || sourceDir.isFile()) {
            System.out.println("Not a valid directory");
            return;
        }
        availableFiles = sourceDir.listFiles();
        if (availableFiles == null) {
            return;
        }
        System.out.println("Total " + availableFiles.length + " files are available");
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        System.out.println("Image Processing Completed");
        System.exit(0);
    }
}
