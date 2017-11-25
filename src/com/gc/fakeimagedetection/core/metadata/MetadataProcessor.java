package com.gc.fakeimagedetection.core.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetadataProcessor {

    File imageFile;
    public static String extracted_data = "";
    Metadata data = null;

    public MetadataProcessor(File imageFile) {
        this.imageFile = imageFile;
        try {
            data = ImageMetadataReader.readMetadata(imageFile);
        } catch (Exception ex) {
            Logger.getLogger(MetadataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (Directory directory : data.getDirectories()) {
            extracted_data += String.format("----------------------------------------------%15s---------------------------------\n", directory.getName());
            for (Tag tag : directory.getTags()) {
                extracted_data += tag + "\n";
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.println("ERROR: " + error);
                }
            }
        }
    }

}
