package com.jmolsmobile.landscapevideocapture;

import android.os.Build;
import android.view.Surface;


public class Utils {
    private static String[] UPSIDE_DOWN_MODEL_LIST = {"Nexus 5X"};

    public static int getOrientationDegree(int rotation) {
        if (isUpsideDown(Build.MODEL)) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    return 90;
                case Surface.ROTATION_90:
                    return 180;
                case Surface.ROTATION_180:
                    return 270;
                case Surface.ROTATION_270:
                    return 0;
                default:
                    throw new IllegalArgumentException("This rotation value is not defined in Surface class.");
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    return 270;
                case Surface.ROTATION_90:
                    return 0;
                case Surface.ROTATION_180:
                    return 90;
                case Surface.ROTATION_270:
                    return 180;
                default:
                    throw new IllegalArgumentException("This rotation value is not defined in Surface class.");
            }
        }
    }

    public static boolean isUpsideDown(String model) {
        for (String str : UPSIDE_DOWN_MODEL_LIST) {
            if (str.equals(model)) return true;
        }

        return false;
    }
}
