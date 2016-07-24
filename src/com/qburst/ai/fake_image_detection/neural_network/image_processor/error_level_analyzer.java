package com.qburst.ai.fake_image_detection.neural_network.image_processor;

import com.qburst.ai.fake_image_detection.neural_network.thread_sync.NotifyingThread;
import ij.ImagePlus;
import ij.io.FileSaver;
import static ij.io.FileSaver.setJpegQuality;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class error_level_analyzer extends NotifyingThread {

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
        c.stretchHistogram(diff, 0.1);
        //        fs = new FileSaver(diff);
//        fs.saveAsPng(elaPath);
//        diff.show();

        ImageProcessor ip = diff.getProcessor();
        ip = ip.resize(200, 200);
        filteredImage = ip.getBufferedImage();
        runningStatus = false;
    }

}
