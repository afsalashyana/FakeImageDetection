package com.qburst.ai.fake_image_detection.neural_network.image_processor;

import com.qburst.ai.fake_image_detection.neural_network.thread_sync.NotifyingThread;
import ij.ImagePlus;
import ij.io.FileSaver;
import static ij.io.FileSaver.setJpegQuality;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class error_level_analyzer extends NotifyingThread {

    String fileLocation;
    int quality = 95;
    Boolean runningStatus = false;
    BufferedImage filteredImage = null;
    Dimension sampledDimension;

    public error_level_analyzer(String fileLocation, int quality) {
        this.fileLocation = fileLocation;
        this.quality = quality;
        this.sampledDimension = new Dimension(100, 100);
    }

    public error_level_analyzer(String fileLocation, int quality, Dimension sampledDimension) {
        this.fileLocation = fileLocation;
        this.quality = quality;
        this.sampledDimension = sampledDimension;
    }

    public void setSampledDimension(Dimension sampledDimension) {
        this.sampledDimension = sampledDimension;
    }

    public Dimension getSampledDimension() {
        return sampledDimension;
    }

    public BufferedImage getFilteredImage() {
        return filteredImage;
    }

    @Override
    public void doRun() {
        runningStatus = true;
        Image img;
        try {
            System.out.println("Loading Image :" + fileLocation);
            img = ImageIO.read(new File(fileLocation));
        } catch (IOException ex) {
            System.err.println("Null Image");
            return;
        }
        System.out.println("Dimensio is set to " + sampledDimension);
        ImagePlus orig = new ImagePlus("Source Image", img);

        String basePath = "/tmp/";
        String origPath = basePath + "-original.jpg";
        String resavedPath = basePath + "-resaved.jpg";
        String elaPath = basePath + "-ELA.png";

        FileSaver fs = new FileSaver(orig);
        setJpegQuality(100);
        fs.saveAsJpeg(origPath);

        setJpegQuality(quality);
        fs.saveAsJpeg(resavedPath);
        ImagePlus resaved = new ImagePlus(resavedPath);

        ImageCalculator calc = new ImageCalculator();
        ImagePlus diff = calc.run("create difference", orig, resaved);
        diff.setTitle("ELA @ " + quality + "%");

        ContrastEnhancer c = new ContrastEnhancer();
        c.stretchHistogram(diff, 0.05);

//        fs = new FileSaver(diff);
//        fs.saveAsPng(elaPath);
//        diff.show();
        ImageProcessor ip = diff.getProcessor();

        ImageProcessor imp;
        if (ip.getWidth() > ip.getHeight()) {
            Rectangle rec = new Rectangle(0, 0, ip.getHeight(), ip.getHeight());
            ip.setRoi(rec);
            imp = ip.crop();
        } else {
            Rectangle rec = new Rectangle(0, 0, ip.getWidth(), ip.getWidth());
            ip.setRoi(rec);
            imp = ip.crop();
        }
        
        
        imp = imp.resize((int) sampledDimension.getWidth(), (int) sampledDimension.getHeight());
        filteredImage = imp.getBufferedImage();
        runningStatus = false;
    }

}
