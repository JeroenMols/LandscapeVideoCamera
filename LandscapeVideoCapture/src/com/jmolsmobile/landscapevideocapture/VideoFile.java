package com.jmolsmobile.landscapevideocapture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoFile {

	private static final String	DATE_FORMAT	= "yyyyMMdd";
	private static final String	DEFAULT_PREFIX		= "video_";
	private static final String	DEFAULT_EXTENSION	= ".mp4";

	public VideoFile(String filename) {

	}

	public String getFilename() {
		final String dateStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
		return DEFAULT_PREFIX + dateStamp + System.nanoTime() + DEFAULT_EXTENSION;
	}

}
