package com.qburst.neural.user_interface;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.qburst.neural.custom_tools.ImageHistogram;
import com.qburst.neural.image_filters.NormalImageFilters;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLDocumentController implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private LineChart<String, Number> LineChart;
    @FXML
    private TextArea textArea;

    Image img;
    BufferedImage image;
    File file = null;

    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void loadImage(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("TIFF", "*.tiff")
        );
        chooser.setInitialDirectory(
                new File("/home/qbuser/Desktop/Machine Learning")
        );
        file = chooser.showOpenDialog(new Stage());
        if (file == null) {
            return;
        }
        try {
            img = new Image(new FileInputStream(file));
            image = ImageIO.read(file);
            imageView.setImage(img);
        } catch (FileNotFoundException ex) {
            System.err.println("Error Occured" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadData(img);

        //Adding Zoom support for the RGB histogram
        final double SCALE_DELTA = 1.1;
        LineChart.setOnScroll(new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                LineChart.setScaleX(LineChart.getScaleX() * scaleFactor);
                LineChart.setScaleY(LineChart.getScaleY() * scaleFactor);
            }
        });

        LineChart.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    LineChart.setScaleX(1.0);
                    LineChart.setScaleY(1.0);
                }
            }
        });
        //Added Zoom to Histogram
    }

    //Called When an Image is chosen
    private void loadData(Image img) {
        LineChart.getData().clear();

        ImageHistogram imageHistogram = new ImageHistogram(img);
        if (imageHistogram.isSuccess()) {
            LineChart.getData().addAll(
                    imageHistogram.getSeriesRed(),
                    imageHistogram.getSeriesGreen(),
                    imageHistogram.getSeriesBlue());
        }
        StringBuilder builder = new StringBuilder();
        StringBuilder criticalData = new StringBuilder();
        try {
            com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(file);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (tag.toString().contains("Photoshop")
                            || tag.toString().contains("GIMP")
                            || tag.toString().contains("Paint")) {
                        criticalData.append(tag + "\n");
                    }
                    builder.append(tag + "\n");
                }
            }

        } catch (Exception e) {
            System.err.println("Metadata Error");
        }
        textArea.setText(builder.toString());
        String cr = new String(criticalData);
        if (cr.length() > 2) {
            JOptionPane.showMessageDialog(null, "Image Tampered With Adobe Photoshop\n" + criticalData, "Fake", JOptionPane.ERROR_MESSAGE);
        }

        //Invoke image filter window;
        new NormalImageFilters(file.getAbsolutePath());
    }

    @FXML
    private void loadAnother(ActionEvent event) {
        //To be added to compare two images
    }
}
