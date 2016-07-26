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
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class Ela_processor extends NotifyingThread {

    String fileLocation;
    String destination = "output/processed";
    int quality = 95;
    Boolean runningStatus = false;
    Dimension sampledDimension;
    ArrayList<String> supportedExtensions;
    int baseCount = 1;

    public Ela_processor(String fileLocation, String destination, int quality) {
        this.fileLocation = fileLocation;
        this.destination = destination + "/";
        this.quality = quality;
    }

    public Ela_processor(String dirLoc, String destination, int quality, ArrayList<String> supportedExtensions, Dimension dimension) {
        this.fileLocation = dirLoc;
        this.quality = quality;
        this.sampledDimension = dimension;
        this.supportedExtensions = supportedExtensions;
        this.destination = destination + "/";
    }

    public void setSampledDimension(Dimension sampledDimension) {
        this.sampledDimension = sampledDimension;
    }

    public Dimension getSampledDimension() {
        return sampledDimension;
    }

    @Override
    public void doRun() {
        try {
            System.out.println("Changing Images to size " + sampledDimension);

            File[] availableFiles = new File(fileLocation).listFiles();
            int totalSize = availableFiles.length;

            Image_converter_ui.progress.setStringPainted(true);
            Image_converter_ui.progress.setMaximum(totalSize);
            int processedSize = 0;
            checkForImageConflict();

            for (File file : availableFiles) {

                String ext = file.getName().split("[.]")[1];
                if (!supportedExtensions.contains(ext)) {
                    System.out.println("Dropping " + file.getName() + " due to unsupported extension --"+ file.getName().split("[.]")[1]);
                    continue;
                }
                runningStatus = true;
                Image img;
                try {
//                    System.out.println("Loading Image :" + file.getAbsolutePath());
                    img = ImageIO.read(file);
                } catch (IOException ex) {
                    System.err.println("Null Image");
                    return;
                }
                ImagePlus orig = new ImagePlus("Source Image", img);
                if(orig.getWidth()<sampledDimension.getWidth()||orig.getHeight()<sampledDimension.getHeight())
                {
                    System.err.println("Too Small to process");
                    continue;
                }

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
                c.stretchHistogram(diff, 0.05);

                ImageProcessor ip = diff.getProcessor();
                ImageProcessor imp;
                if(ip.getWidth()>ip.getHeight())
                {
                    Rectangle rec = new Rectangle(0, 0, ip.getHeight(), ip.getHeight());
                    ip.setRoi(rec);
                    imp = ip.crop();
                }else{
                    Rectangle rec = new Rectangle(0, 0, ip.getWidth(), ip.getWidth());
                    ip.setRoi(rec);
                    imp = ip.crop();                    
                }
                imp = imp.resize((int) sampledDimension.getWidth(), (int) sampledDimension.getHeight());
                FileSaver resultSaver = new FileSaver(new ImagePlus("Result", imp.getBufferedImage()));

                String savePath = destination + Image_converter_ui.bName + (baseCount + processedSize) + ".png";
                resultSaver.saveAsPng(savePath);

                runningStatus = false;
                double percentage = ((processedSize + 1) / totalSize);

                Image_converter_ui.progress.setValue(processedSize);
                Image_converter_ui.progress.setString(String.format("%.1f", (percentage*100)) + "%" + processedSize+ "/" + totalSize);

                processedSize++;
            }
        } catch (Exception e) {
            System.err.println("Error Occured at Ela_processor :");
            e.printStackTrace();
        }
    }

    private void checkForImageConflict() {
        baseCount = 1;
        while (true) {
            String savePath = destination + Image_converter_ui.bName + (baseCount + 1) + ".png";
            if (new File(savePath).exists()) {
                baseCount++;
            } else {
                break;
            }
        }
        System.out.println("There are " + (baseCount - 1) + " images already. Starting from " + baseCount);
    }

}
