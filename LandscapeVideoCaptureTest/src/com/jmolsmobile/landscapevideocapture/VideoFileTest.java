package com.jmolsmobile.landscapevideocapture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import android.os.Environment;

import com.jmolsmobile.landscapevideocapture.VideoFile;

public class VideoFileTest extends TestCase {

	public void test_canCreateObject() {
		new VideoFile("");
	}

	public void test_filenameShouldNotBeNull() {
		final VideoFile videoFile = new VideoFile("");
		assertNotNull(getFilename(videoFile));
	}

	public void test_filenameShouldEndWithExtension() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(getFilename(videoFile).endsWith(".mp4"));
	}

	public void test_filenameShouldStartWithVideo() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(getFilename(videoFile).startsWith("video"));
	}

	public void test_filenameShouldBeUnique() {
		final VideoFile videoFile1 = new VideoFile("", new Date());
		final VideoFile videoFile2 = new VideoFile("", new Date(System.currentTimeMillis() + 1000));
		assertFalse(getFilename(videoFile1).equals(getFilename(videoFile2)));
	}

	public void test_filenameShouldContainDateAndTime() {
		final Date date = new Date();
		final VideoFile videoFile = new VideoFile("", date);
		final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
		assertTrue(getFilename(videoFile).contains("_" + dateAndTimeString));
	}

	public void test_filenameShouldContainSpecifiedDateAndTime() {
		final Date date = new Date(0);
		final VideoFile videoFile = new VideoFile("", date);
		final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
		assertTrue(getFilename(videoFile).contains(dateAndTimeString));
	}

	public void test_filenameCanBeSpecified() {
		final String expectedFilename = "test.mp4";
		final VideoFile videoFile = new VideoFile(expectedFilename);
		assertEquals(getFilename(videoFile), expectedFilename);
	}

	public void test_filenameIsDefaultWhenSpecifiedNull() {
		final VideoFile videoFile = new VideoFile(null);
		assertTrue(getFilename(videoFile).matches("video_[0-9]{8}_[0-9]{6}\\.mp4"));
	}

	public void test_fileShouldContainPathToVideoFolder() {
		final VideoFile videoFile = new VideoFile("");
		final String expectedPath = Environment.DIRECTORY_MOVIES;
		assertTrue(videoFile.getFile().getAbsolutePath().contains(expectedPath));
	}

	private String getFilename(final VideoFile videoFile) {
		return videoFile.getFile().getName();
	}
}
