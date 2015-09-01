/**
 * Copyright 2014 Jeroen Mols
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture.configuration;

import android.support.test.runner.AndroidJUnit4;

import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureResolution;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CaptureConfigurationTest {

    @Test
    public void defaultConfiguration() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();

        checkConfiguration(config, CaptureResolution.RES_720P.width, CaptureResolution.RES_720P.height,
                CaptureResolution.RES_720P.getBitrate(CaptureQuality.HIGH), CaptureConfiguration.NO_DURATION_LIMIT,
                CaptureConfiguration.NO_FILESIZE_LIMIT);
    }

    @Test
    public void predefinedConfiguration() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration(CaptureResolution.RES_2160P, CaptureQuality.LOW);

        checkConfiguration(config, CaptureResolution.RES_2160P.width, CaptureResolution.RES_2160P.height,
                CaptureResolution.RES_2160P.getBitrate(CaptureQuality.LOW), CaptureConfiguration.NO_DURATION_LIMIT,
                CaptureConfiguration.NO_FILESIZE_LIMIT);
    }

    @Test
    public void predefinedConfigurationWithSizeDuration() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration(CaptureResolution.RES_1080P,
                CaptureQuality.MEDIUM, 50, 10);

        checkConfiguration(config, CaptureResolution.RES_1080P.width, CaptureResolution.RES_1080P.height,
                CaptureResolution.RES_1080P.getBitrate(CaptureQuality.MEDIUM), 50 * 1000, 10 * 1024 * 1024);
    }

    @Test
    public void configurationWithHeightWidthBitrate() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration(200, 300, 5000000);

        checkConfiguration(config, 200, 300, 5000000, CaptureConfiguration.NO_DURATION_LIMIT,
                CaptureConfiguration.NO_FILESIZE_LIMIT);
    }

    @Test
    public void configurationWithHeightWidthBitrateSizeDuration() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration(200, 300, 5000000, 1, 100);

        checkConfiguration(config, 200, 300, 5000000, 1 * 1000, 100 * 1024 * 1024);
    }

    private void checkConfiguration(final CaptureConfiguration config, int width, int height, int bitrate,
                                    int duration, int filesize) {
        assertEquals(config.getVideoWidth(), width);
        assertEquals(config.getVideoHeight(), height);
        assertEquals(config.getVideoBitrate(), bitrate);
        assertEquals(config.getMaxCaptureDuration(), duration);
        assertEquals(config.getMaxCaptureFileSize(), filesize);
    }
}
