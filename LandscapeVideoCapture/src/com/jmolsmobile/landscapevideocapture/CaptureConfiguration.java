package com.jmolsmobile.landscapevideocapture;

/**
 * @author Jeroen Mols
 */
public class CaptureConfiguration {

	// TODO determine default values

	private final int	PREVIEW_VIDEO_WIDTH		= 640;
	private final int	PREVIEW_VIDEO_HEIGHT	= 480;
	private final int	CAPTURE_VIDEO_WIDTH		= 640;
	private final int	CAPTURE_VIDEO_HEIGHT	= 480;
	private final int	BITRATE_PER_SECOND		= 750000;
	private final int	MAX_CAPTURE_DURATION	= 30 * 1000;
	private final int	MAX_CAPTURE_FILESIZE	= 10 * 1024 * 1024;

	public CaptureConfiguration() {
	}

	/**
	 * @return Width of the camera preview in pixels
	 */
	public int getPreviewWidth() {
		return PREVIEW_VIDEO_WIDTH;
	}

	/**
	 * @return Height of the camera preview in pixels
	 */
	public int getPreviewHeight() {
		return PREVIEW_VIDEO_HEIGHT;
	}

	/**
	 * @return Width of the captured video in pixels
	 */
	public int getVideoWidth() {
		return CAPTURE_VIDEO_WIDTH;
	}

	/**
	 * @return Height of the captured video in pixels
	 */
	public int getVideoHeight() {
		return CAPTURE_VIDEO_HEIGHT;
	}

	/**
	 * @return Bitrate of the captured video in bits per second
	 */
	public int getBitratePerSecond() {
		return BITRATE_PER_SECOND;
	}

	/**
	 * @return Maximum duration of the captured video in milliseconds
	 */
	public int getMaxCaptureDuration() {
		return MAX_CAPTURE_DURATION;
	}

	/**
	 * @return Maximum filesize of the captured video in bytes
	 */
	public int getMaxCaptureFileSize() {
		return MAX_CAPTURE_FILESIZE;
	}

}