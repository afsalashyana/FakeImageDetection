package com.gc.fakeimagedetection.core.errorlevelanalysis;

import com.gc.fakeimagedetection.core.listener.ErrorLevelAnalysisListener;
import com.gc.fakeimagedetection.core.multithread.NotifyingThread;
import com.gc.fakeimagedetection.ui.alert.Calert;
import ij.ImagePlus;
import ij.io.FileSaver;
import static ij.io.FileSaver.setJpegQuality;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javax.imageio.ImageIO;

public class FIDErrorLevelAnalysis extends NotifyingThread {

    String fileLocation;
    int quality = 95;
    Boolean runningStatus = false;
    Dimension sampledDimension;
    ErrorLevelAnalysisListener listener;

    public FIDErrorLevelAnalysis(String fileLocation,
            int quality, Dimension sampledDimension, ErrorLevelAnalysisListener listener) {
        this.fileLocation = fileLocation;
        this.quality = quality;
        this.sampledDimension = sampledDimension;
        this.listener = listener;
    }

    @Override
    public void doRun() {
        try {
            Image img;
            try {
                System.out.println("Loading Image :" + fileLocation);
                img = ImageIO.read(new File(fileLocation));
            } catch (IOException ex) {
                System.err.println("Null Image");
                return;
            }
            System.out.println("Dimension is set to " + sampledDimension);
            ImagePlus orig = new ImagePlus("Source Image", img);

            String basePath = "tmp/";
            File file = new File(basePath);
            if (!file.exists()) {
                file.mkdir();
            }
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
            listener.elaCompleted(imp.getBufferedImage());
        } catch (Exception e) {
            //JavaFX UI thread is different from current Thread
            System.err.println(e.toString());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Calert.showAlert("Fatal Error Occured", "Neural Network Error", Alert.AlertType.ERROR);
                }
            });
            listener.elaCompleted(null);
        }
    }

}
