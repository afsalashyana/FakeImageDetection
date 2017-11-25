package com.gc.fakeimagedetection.core.constants;

public class ConstantObjects {

    public static String[] supportedExtensions
            = {"JPG", "jpg", "JPEG", "PNG", "png", "TIFF", "TIF", "tif"};
    public static String neuralNetworkPath = "nnet/MLPV2.0.nnet";
    public static Boolean shouldPropogateResult = true;
    public static float fakeness = -1;
    public static float realness = -1;
}
