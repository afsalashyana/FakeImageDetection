package com.gc.fakeimagedetection.core;

import com.gc.fakeimagedetection.core.errorlevelanalysis.ImageStandardizer;
import java.awt.Dimension;
import java.io.File;

public class FIDPreprocessor {

    public static void main(String[] args) {
        if (args == null || args.length < 5) {
            System.err.println("Usage : <sourceDir> <destDir> <sWidth> <sHeight> <output label>");
            return;
        }
        String srcDir = args[0];
        String destDir = args[1];
        String sWidth = args[2];
        String sHeight = args[3];
        String outLabel = args[4];

        ImageStandardizer preprocessor
                = new ImageStandardizer(new File(srcDir), new File(destDir),
                        new Dimension(Integer.parseInt(sWidth), Integer.parseInt(sHeight)), outLabel);
    }
}
