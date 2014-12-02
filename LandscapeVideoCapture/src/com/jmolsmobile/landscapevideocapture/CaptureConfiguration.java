package com.jmolsmobile.landscapevideocapture;

import android.media.MediaRecorder;

/**
 * @author Jeroen Mols
 */
public class CaptureConfiguration {

	public static final int		NO_DURATION_LIMIT		= -1;
	public static final int		NO_FILESIZE_LIMIT		= -1;

	private final int			PREVIEW_VIDEO_WIDTH		= PredefinedCaptureConfigurations.WIDTH_720P;
	private final int			PREVIEW_VIDEO_HEIGHT	= PredefinedCaptureConfigurations.HEIGHT_720P;
	private final int			CAPTURE_VIDEO_WIDTH		= PredefinedCaptureConfigurations.WIDTH_720P;
	private final int			CAPTURE_VIDEO_HEIGHT	= PredefinedCaptureConfigurations.HEIGHT_720P;
	private final int			BITRATE_PER_SECOND		= PredefinedCaptureConfigurations.BITRATE_HQ_720P;
	private final int			MAX_CAPTURE_DURATION	= NO_DURATION_LIMIT;
	private final int			MAX_CAPTURE_FILESIZE	= NO_FILESIZE_LIMIT;

	private static final int	OUTPUT_FORMAT			= MediaRecorder.OutputFormat.MPEG_4;
	private static final int	AUDIO_SOURCE			= MediaRecorder.AudioSource.DEFAULT;
	private static final int	AUDIO_ENCODER			= MediaRecorder.AudioEncoder.AAC;
	private static final int	VIDEO_SOURCE			= MediaRecorder.VideoSource.CAMERA;
	private static final int	VIDEO_ENCODER			= MediaRecorder.VideoEncoder.H264;

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
	public int getVideoBitrate() {
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

	public int getOutputFormat() {
		return OUTPUT_FORMAT;
	}

	public int getAudioSource() {
		return AUDIO_SOURCE;
	}

	public int getAudioEncoder() {
		return AUDIO_ENCODER;
	}

	public int getVideoSource() {
		return VIDEO_SOURCE;
	}

	public int getVideoEncoder() {
		return VIDEO_ENCODER;
	}

}