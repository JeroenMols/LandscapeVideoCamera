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

package com.jmolsmobile.landscapevideocapture;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VideoFileTest {

    @Test
    public void canCreateObject() {
        new VideoFile("");
    }

    @Test
    public void filenameShouldNotBeNull() {
        final VideoFile videoFile = new VideoFile("");
        assertNotNull(videoFile.getFile().getName());
    }

    @Test
    public void filenameShouldEndWithExtension() {
        final VideoFile videoFile = new VideoFile("");
        assertTrue(videoFile.getFile().getName().endsWith(".mp4"));
    }

    @Test
    public void filenameShouldStartWithVideo() {
        final VideoFile videoFile = new VideoFile("");
        assertTrue(videoFile.getFile().getName().startsWith("video"));
    }

    @Test
    public void filenameShouldBeUnique() {
        final VideoFile videoFile1 = new VideoFile("", new Date());
        final VideoFile videoFile2 = new VideoFile("", new Date(System.currentTimeMillis() + 1000));
        assertFalse(videoFile1.getFile().getName().equals(videoFile2.getFile().getName()));
    }

    @Test
    public void filenameShouldContainDateAndTime() {
        final Date date = new Date();
        final VideoFile videoFile = new VideoFile("", date);
        final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
        assertTrue(videoFile.getFile().getName().contains("_" + dateAndTimeString));
    }

    @Test
    public void filenameShouldContainSpecifiedDateAndTime() {
        final Date date = new Date(0);
        final VideoFile videoFile = new VideoFile("", date);
        final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
        assertTrue(videoFile.getFile().getName().contains(dateAndTimeString));
    }

    @Test
    public void filenameCanBeSpecified() {
        final String expectedFilename = "test.mp4";
        final VideoFile videoFile = new VideoFile(expectedFilename);
        assertEquals(videoFile.getFile().getName(), expectedFilename);
    }

    @Test
    public void filenameIsDefaultWhenSpecifiedNull() {
        final VideoFile videoFile = new VideoFile(null);
        assertTrue(videoFile.getFile().getName().matches("video_[0-9]{8}_[0-9]{6}\\.mp4"));
    }

    @Test
    public void fileShouldContainPathToVideoFolder() {
        final VideoFile videoFile = new VideoFile("");
        final String expectedPath = Environment.DIRECTORY_MOVIES;
        assertTrue(videoFile.getFullPath().contains(expectedPath));
    }

    @Test
    public void fileShouldNotStartWithDoublePath() {
        final String expectedPath = "sdcard/videofile.mp4";
        final VideoFile videoFile = new VideoFile(expectedPath);
        assertTrue(videoFile.getFullPath().contains(expectedPath));
    }
}
