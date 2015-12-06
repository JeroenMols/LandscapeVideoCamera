package com.jmolsmobile.landscapevideocapture.camera;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

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

}
