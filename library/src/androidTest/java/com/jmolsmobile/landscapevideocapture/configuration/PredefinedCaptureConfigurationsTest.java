package com.jmolsmobile.landscapevideocapture.configuration;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Jeroen Mols on 12/12/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class PredefinedCaptureConfigurationsTest {

    @Test
    public void instantiateClassForCodeCoverage() throws Exception {
        new PredefinedCaptureConfigurations();
    }
}
