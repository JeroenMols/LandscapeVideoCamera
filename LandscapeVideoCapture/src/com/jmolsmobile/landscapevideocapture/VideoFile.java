package com.jmolsmobile.landscapevideocapture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class VideoFile {

	private static final String	DATE_FORMAT			= "yyyyMMdd_HHmmss";
	private static final String	DEFAULT_PREFIX		= "video_";
	private static final String	DEFAULT_EXTENSION	= ".mp4";

	private final String		filename;
	private Date				date;

	public VideoFile(String filename) {
		this.filename = filename;
	}

	public VideoFile(String filename, Date date) {
		this(filename);
		this.date = date;
	}

	public File getFile() {
		return new File(Environment.DIRECTORY_MOVIES, getFilename());
	}

	private String getFilename() {
		if (isValidFilename()) return filename;

		final String dateStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(getDate());
		return DEFAULT_PREFIX + dateStamp + DEFAULT_EXTENSION;
	}

	private boolean isValidFilename() {
		if (filename == null) return false;
		if (filename.isEmpty()) return false;

		return true;
	}

	private Date getDate() {
		if (date == null) {
			date = new Date();
		}
		return date;
	}
}
