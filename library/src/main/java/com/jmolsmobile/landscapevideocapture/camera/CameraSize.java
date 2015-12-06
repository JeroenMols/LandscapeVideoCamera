package com.jmolsmobile.landscapevideocapture.camera;

/**
 * Created by Jeroen Mols on 06/12/15.
 */
public class CameraSize {

    private final int width;
    private final int height;

    public CameraSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
