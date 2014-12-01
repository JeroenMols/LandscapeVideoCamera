package com.jmolsmobile.landscapevideocapture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

/**
 * @author Jeroen Mols
 * @date 22 November 2014
 */
public class VideoFile {

	private static final String	DIRECTORY_SEPARATOR	= "/";
	private static final String	DATE_FORMAT			= "yyyyMMdd_HHmmss";
	private static final String	DEFAULT_PREFIX		= "video_";
	private static final String	DEFAULT_EXTENSION	= ".mp4";

	private final String		mFilename;
	private Date				mDate;

	public VideoFile(String filename) {
		this.mFilename = filename;
	}

	public VideoFile(String filename, Date date) {
		this(filename);
		this.mDate = date;
	}

	public String getFullPath() {
		return getFile().getAbsolutePath();
	}

	public File getFile() {
		final String filename = generateFilename();
		if (filename.contains(DIRECTORY_SEPARATOR)) return new File(filename);

		final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		path.mkdirs();
		return new File(path, generateFilename());
	}

	private String generateFilename() {
		if (isValidFilename()) return mFilename;

		final String dateStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(getDate());
		return DEFAULT_PREFIX + dateStamp + DEFAULT_EXTENSION;
	}

	private boolean isValidFilename() {
		if (mFilename == null) return false;
		if (mFilename.isEmpty()) return false;

		return true;
	}

	private Date getDate() {
		if (mDate == null) {
			mDate = new Date();
		}
		return mDate;
	}
}
