package com.jmolsmobile.landscapevideocapture;

public class VideoFile {

	private static final String	DEFAULT_PREFIX	= "video";
	private static final String	DEFAULT_EXTENSION	= ".mp4";

	public VideoFile(String filename) {

	}

	public String getFilename() {
		return DEFAULT_PREFIX + System.nanoTime() + DEFAULT_EXTENSION;
	}

}
