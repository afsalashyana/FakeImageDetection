package com.qburst.fakeimagedetection.core.errorlevelanalysis;

import com.qburst.fakeimagedetection.core.multithread.ThreadCompleteListener;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

public final class ImageStandardizer implements ThreadCompleteListener {

    File sourceDir = null;
    File destDir = null;
    Dimension resolution = null;
    File[] availableFiles = null;
    ArrayList<String> supportedExtensions;

    public ImageStandardizer(File sourceDirectory, File destinationDirectory, Dimension finalResolution) {
        System.out.println("Starting new Image Standardizer");
        sourceDir = sourceDirectory;
        resolution = finalResolution;
        destDir = destinationDirectory;
        init();
        loadDirectory(sourceDir.getAbsolutePath());
        processImages();
    }

    void init() {
        supportedExtensions = new ArrayList<>();
        supportedExtensions.add("jpg");
        supportedExtensions.add("JPG");
        supportedExtensions.add("png");
        supportedExtensions.add("JPEG");
        supportedExtensions.add("PNG");
        supportedExtensions.add("TIF");
        supportedExtensions.add("tif");
    }

    void processImages() {
        System.out.println("Calling Processor with dimension " + resolution);
        ErrorLevelAnalyzer imageProcessor
                = new ErrorLevelAnalyzer(sourceDir.getAbsolutePath(), destDir.getAbsolutePath(), 95, supportedExtensions, resolution);
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

    public static void main(String[] args) {
        ImageStandardizer image_standardizer = new ImageStandardizer(new File("/home/qbuser/Pictures/Wallpapers"), new File("output/"), new Dimension(200, 200));
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        System.out.println("Starting new Image Standardizer");
        System.out.println("Image Processing Completed");
        System.exit(0);
    }
}
