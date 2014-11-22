package com.jmolsmobile.landscapevideocapture.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import com.jmolsmobile.landscapevideocapture.VideoFile;

public class VideoFileTest extends TestCase {

	public void test_canCreateObject() {
		new VideoFile("");
	}

	public void test_filenameShouldNotBeNull() {
		final VideoFile videoFile = new VideoFile("");
		assertNotNull(videoFile.getFilename());
	}

	public void test_filenameShouldEndWithExtension() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(videoFile.getFilename().endsWith(".mp4"));
	}

	public void test_filenameShouldStartWithVideo() {
		final VideoFile videoFile = new VideoFile("");
		assertTrue(videoFile.getFilename().startsWith("video"));
	}

	public void test_filenameShouldBeUnique() {
		final VideoFile videoFile1 = new VideoFile("");
		final VideoFile videoFile2 = new VideoFile("");
		assertFalse(videoFile1.getFilename().equals(videoFile2.getFilename()));
	}

	public void test_filenameShouldContainDateAndTime() {
		final Date date = new Date();
		final VideoFile videoFile = new VideoFile("", date);
		final String dateAndTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		assertTrue(videoFile.getFilename().contains("_" + dateAndTimeString));
	}

}
