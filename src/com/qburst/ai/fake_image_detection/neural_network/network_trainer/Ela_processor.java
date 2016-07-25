package com.qburst.ai.fake_image_detection.neural_network.network_trainer;

import com.qburst.ai.fake_image_detection.neural_network.thread_sync.NotifyingThread;
import ij.ImagePlus;
import ij.io.FileSaver;
import static ij.io.FileSaver.setJpegQuality;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class Ela_processor extends NotifyingThread {

    String fileLocation;
    int quality = 95;
    Boolean runningStatus = false;
    Dimension sampledDimension;
    ArrayList<String> supportedExtensions;

    public Ela_processor(String fileLocation, int quality) {
        this.fileLocation = fileLocation;
        this.quality = quality;
    }

    public Ela_processor(String dirLoc, int quality, ArrayList<String> supportedExtensions, Dimension dimension) {
        this.fileLocation = dirLoc;
        this.quality = quality;
        this.sampledDimension = dimension;
        this.supportedExtensions = supportedExtensions;
    }

    public void setSampledDimension(Dimension sampledDimension) {
        this.sampledDimension = sampledDimension;
    }

    public Dimension getSampledDimension() {
        return sampledDimension;
    }
    
    
    @Override
    public void doRun() {
        System.out.println("Changing Images to size " + sampledDimension);
        
        File[] availableFiles = new File(fileLocation).listFiles();
        int totalSize = availableFiles.length;
  
        Image_converter_ui.progress.setStringPainted(true);
        Image_converter_ui.progress.setMaximum(totalSize);
        int processedSize=0;
        
        
        for (File file : availableFiles) {
            String ext = file.getName().split("[.]")[1];
            if (!supportedExtensions.contains(ext)) {
                System.out.println("Dropping " + file.getName() + " due to unsupported extension");
                return;
            }
            try {
                runningStatus = true;
                Image img;
                try {
                    System.out.println("Loading Image :" + file.getAbsolutePath());
                    img = ImageIO.read(file);
                } catch (IOException ex) {
                    System.err.println("Null Image");
                    return;
                }
                ImagePlus orig = new ImagePlus("Source Image", img);

                String basePath = "output/";
                String origPath = basePath + "original.jpg";
                String resavedPath = basePath + "resaved.jpg";
                String elaPath = basePath + "ELA.png";

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

                ImageProcessor ip = diff.getProcessor();
                ip = ip.resize((int) sampledDimension.getWidth(), (int) sampledDimension.getHeight());
                FileSaver resultSaver = new FileSaver(new ImagePlus("Result",ip.getBufferedImage()));
                
                String savePath = "output/processed/" + Image_converter_ui.bName +(processedSize+1) + ".png";
                resultSaver.saveAsPng(savePath);
//                System.out.println("Saved " + file.getName() + " to " + savePath);
               
                runningStatus = false;
                DecimalFormat format = new DecimalFormat(".##");
                double percentage = (++processedSize)/totalSize;
                
                Image_converter_ui.progress.setValue(processedSize);
                Image_converter_ui.progress.setString(String.format("%.d", percentage));
                
            } catch (Exception ex) {
                System.out.println("Error Occured during processing" + ex.getLocalizedMessage());
            }
        }
    }

}
