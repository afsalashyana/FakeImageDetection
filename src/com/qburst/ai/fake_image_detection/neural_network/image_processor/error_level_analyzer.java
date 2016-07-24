package com.qburst.ai.fake_image_detection.neural_network.image_processor;

import ij.ImagePlus;
import ij.io.FileSaver;
import static ij.io.FileSaver.setJpegQuality;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import java.awt.image.BufferedImage;

public class error_level_analyzer implements Runnable{
    String fileLocation;
    int quality = 95;
    Boolean runningStatus = false;
    BufferedImage filteredImage = null;

    public error_level_analyzer(String fileLocation, int quality) {
        this.fileLocation = fileLocation;
        this.quality = quality;
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }
    
    @Override
    public void run() {
        runningStatus = true;
        ImagePlus orig = new ImagePlus(fileLocation);

        String basePath = System.getProperty("user.dir") + "/" + orig.getTitle();
        String origPath = basePath + "-original.jpg";
        String resavedPath = basePath +  "-resaved.jpg";
        String elaPath =basePath  + "-ELA.png";

        FileSaver fs = new FileSaver(null);
        setJpegQuality(100);
        fs.saveAsJpeg(origPath);

        setJpegQuality(quality);
        fs.saveAsJpeg(resavedPath);
        ImagePlus resaved = new ImagePlus(resavedPath);

        ImageCalculator calc = new ImageCalculator();
        ImagePlus diff = calc.run("create difference", orig, resaved);
        diff.setTitle("ELA @ " + quality + "%");

        ContrastEnhancer c = new ContrastEnhancer();
//        c.stretchHistogram(diff, 0.1);

        fs = new FileSaver(diff);
        fs.saveAsPng(elaPath);

        diff.show();
        filteredImage = diff.getBufferedImage();
        runningStatus = false;
    }
    
}
