package com.qburst.ai.fake_image_detection.neural_network.core.training;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.neuroph.core.data.DataSet;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageUtilities;

public class Nn_trainer {

    File srcDirectory;
    Dimension sampleDimension;
    ArrayList<String> imageLabels;

    public Nn_trainer(File srcDirectory, Dimension sampleDimension, ArrayList<String> imageLabels) {
        this.srcDirectory = srcDirectory;
        this.sampleDimension = sampleDimension;
        this.imageLabels = imageLabels;
    }

    public void startTraining() {
        HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage>();
        for (File file : srcDirectory.listFiles()) {
            imageLabels.add(FilenameUtils.removeExtension(file.getName()));
            if (sampleDimension.getWidth() > 0 && sampleDimension.getHeight() > 0) {
                Double w = sampleDimension.getWidth();
                Double h = sampleDimension.getHeight();
                imagesMap.put(file.getName(), ImageUtilities.resizeImage(ImageUtilities.loadImage(file), w.intValue(), h.intValue()));
            }
        }
        Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages(imagesMap);
        DataSet learningData = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, imageRgbData);

    }

}
