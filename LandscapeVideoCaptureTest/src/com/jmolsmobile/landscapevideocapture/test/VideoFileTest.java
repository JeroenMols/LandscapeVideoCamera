package com.jmolsmobile.landscapevideocapture.test;

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
}
