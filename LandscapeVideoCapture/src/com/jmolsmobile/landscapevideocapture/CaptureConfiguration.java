package com.jmolsmobile.landscapevideocapture;

import android.media.MediaRecorder;

/**
 * @author Jeroen Mols
 */
public class CaptureConfiguration {

	public static final int		NO_DURATION_LIMIT		= -1;
	public static final int		NO_FILESIZE_LIMIT		= -1;

	public static final int		BITRATE_HQ_2160P		= 40000000;
	public static final int		BITRATE_MQ_2160P		= 28000000;
	public static final int		BITRATE_LQ_2160P		= 12000000;
	public static final int		BITRATE_HQ_1440P		= 10000000;
	public static final int		BITRATE_MQ_1440P		= 7000000;
	public static final int		BITRATE_LQ_1440P		= 3000000;
	public static final int		BITRATE_HQ_1080P		= 8000000;
	public static final int		BITRATE_MQ_1080P		= 5600000;
	public static final int		BITRATE_LQ_1080P		= 2400000;
	public static final int		BITRATE_HQ_720P			= 5000000;
	public static final int		BITRATE_MQ_720P			= 3500000;
	public static final int		BITRATE_LQ_720P			= 1500000;
	public static final int		BITRATE_HQ_480P			= 2500000;
	public static final int		BITRATE_MQ_480P			= 1750000;
	public static final int		BITRATE_LQ_480P			= 750000;
	public static final int		BITRATE_HQ_360P			= 1000000;
	public static final int		BITRATE_MQ_360P			= 700000;
	public static final int		BITRATE_LQ_360P			= 300000;

	private final int			PREVIEW_VIDEO_WIDTH		= 1280;
	private final int			PREVIEW_VIDEO_HEIGHT	= 720;
	private final int			CAPTURE_VIDEO_WIDTH		= 1280;
	private final int			CAPTURE_VIDEO_HEIGHT	= 720;
	private final int			BITRATE_PER_SECOND		= BITRATE_HQ_720P;
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