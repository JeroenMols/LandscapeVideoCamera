/**
 * Copyright 2014 Jeroen Mols
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture;

import android.os.Environment;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoFileTest extends TestCase {

	public void test_canCreateObject() {
		new VideoFile("");
	}

	public void test_filenameShouldNotBeNull() {
		final VideoFile videoFile = new VideoFile("");
		assertNotNull(videoFile.getFile().getName());
	}

	public void test_filenameShouldEndWithExtension() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(videoFile.getFile().getName().endsWith(".mp4"));
	}

	public void test_filenameShouldStartWithVideo() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(videoFile.getFile().getName().startsWith("video"));
	}

	public void test_filenameShouldBeUnique() {
		final VideoFile videoFile1 = new VideoFile("", new Date());
		final VideoFile videoFile2 = new VideoFile("", new Date(System.currentTimeMillis() + 1000));
		assertFalse(videoFile1.getFile().getName().equals(videoFile2.getFile().getName()));
	}

	public void test_filenameShouldContainDateAndTime() {
		final Date date = new Date();
		final VideoFile videoFile = new VideoFile("", date);
		final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
		assertTrue(videoFile.getFile().getName().contains("_" + dateAndTimeString));
	}

	public void test_filenameShouldContainSpecifiedDateAndTime() {
		final Date date = new Date(0);
		final VideoFile videoFile = new VideoFile("", date);
		final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
		assertTrue(videoFile.getFile().getName().contains(dateAndTimeString));
	}

	public void test_filenameCanBeSpecified() {
		final String expectedFilename = "test.mp4";
		final VideoFile videoFile = new VideoFile(expectedFilename);
		assertEquals(videoFile.getFile().getName(), expectedFilename);
	}

	public void test_filenameIsDefaultWhenSpecifiedNull() {
		final VideoFile videoFile = new VideoFile(null);
		assertTrue(videoFile.getFile().getName().matches("video_[0-9]{8}_[0-9]{6}\\.mp4"));
	}

	public void test_fileShouldContainPathToVideoFolder() {
		final VideoFile videoFile = new VideoFile("");
		final String expectedPath = Environment.DIRECTORY_MOVIES;
		assertTrue(videoFile.getFullPath().contains(expectedPath));
	}

	public void test_fileShouldNotStartWithDoublePath() {
		final String expectedPath = "sdcard/videofile.mp4";
		final VideoFile videoFile = new VideoFile(expectedPath);
		assertTrue(videoFile.getFullPath().contains(expectedPath));
	}
}
