package com.jmolsmobile.landscapevideocapture.camera;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Jeroen Mols on 06/12/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CameraSizeTest {

    @Test
    public void canInstantiate() throws Exception {
        new CameraSize(0, 0);
    }

    @Test
    public void returnWidth() throws Exception {
        CameraSize cameraSize = new CameraSize(800, 600);

        assertEquals(800, cameraSize.getWidth());
    }

    @Test
    public void returnHeight() throws Exception {
        CameraSize cameraSize = new CameraSize(800, 600);

        assertEquals(600, cameraSize.getHeight());
    }
}
