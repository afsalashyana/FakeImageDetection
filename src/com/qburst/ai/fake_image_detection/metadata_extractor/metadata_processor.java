package com.qburst.ai.fake_image_detection.metadata_extractor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class metadata_processor {

    File imageFile;
    String extracted_data;
    Metadata data = null;

    public metadata_processor(File imageFile) {
        this.imageFile = imageFile;
        try {
            data = ImageMetadataReader.readMetadata(imageFile);
        } catch (Exception ex) {
            Logger.getLogger(metadata_processor.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Directory directory : data.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.println("ERROR: " + error);
                }
            }
        }
    }

}
