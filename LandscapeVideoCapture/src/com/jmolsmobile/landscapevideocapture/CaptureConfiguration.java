package com.jmolsmobile.landscapevideocapture;

import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;

import com.jmolsmobile.landscapevideocapture.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.PredefinedCaptureConfigurations.CaptureResolution;

/**
 * @author Jeroen Mols
 */
public class CaptureConfiguration implements Parcelable {

	public static final int	NO_DURATION_LIMIT		= -1;
	public static final int	NO_FILESIZE_LIMIT		= -1;

	private int				PREVIEW_VIDEO_WIDTH		= PredefinedCaptureConfigurations.WIDTH_720P;
	private int				PREVIEW_VIDEO_HEIGHT	= PredefinedCaptureConfigurations.HEIGHT_720P;
	private int				mVideoWidth				= PredefinedCaptureConfigurations.WIDTH_720P;
	private int				mVideoHeight			= PredefinedCaptureConfigurations.HEIGHT_720P;
	private int				mBitrate				= PredefinedCaptureConfigurations.BITRATE_HQ_720P;
	private int				MAX_CAPTURE_DURATION	= NO_DURATION_LIMIT;
	private int				MAX_CAPTURE_FILESIZE	= NO_FILESIZE_LIMIT;

	private int				OUTPUT_FORMAT			= MediaRecorder.OutputFormat.MPEG_4;
	private int				AUDIO_SOURCE			= MediaRecorder.AudioSource.DEFAULT;
	private int				AUDIO_ENCODER			= MediaRecorder.AudioEncoder.AAC;
	private int				VIDEO_SOURCE			= MediaRecorder.VideoSource.CAMERA;
	private int				VIDEO_ENCODER			= MediaRecorder.VideoEncoder.H264;

	public CaptureConfiguration() {
		// Default configuration
	}

	public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality) {
		mVideoWidth = resolution.width;
		mVideoHeight = resolution.height;
		mBitrate = resolution.getBitrate(quality);
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
		return mVideoWidth;
	}

	/**
	 * @return Height of the captured video in pixels
	 */
	public int getVideoHeight() {
		return mVideoHeight;
	}

	/**
	 * @return Bitrate of the captured video in bits per second
	 */
	public int getVideoBitrate() {
		return mBitrate;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(PREVIEW_VIDEO_WIDTH);
		dest.writeInt(PREVIEW_VIDEO_HEIGHT);
		dest.writeInt(mVideoWidth);
		dest.writeInt(mVideoHeight);
		dest.writeInt(mBitrate);
		dest.writeInt(MAX_CAPTURE_DURATION);
		dest.writeInt(MAX_CAPTURE_FILESIZE);

		dest.writeInt(OUTPUT_FORMAT);
		dest.writeInt(AUDIO_SOURCE);
		dest.writeInt(AUDIO_ENCODER);
		dest.writeInt(VIDEO_SOURCE);
		dest.writeInt(VIDEO_ENCODER);
	}

	public static final Parcelable.Creator<CaptureConfiguration>	CREATOR	= new Parcelable.Creator<CaptureConfiguration>() {
																				@Override
																				public CaptureConfiguration createFromParcel(
																						Parcel in) {
																					return new CaptureConfiguration(in);
																				}

																				@Override
																				public CaptureConfiguration[] newArray(
																						int size) {
																					return new CaptureConfiguration[size];
																				}
																			};

	private CaptureConfiguration(Parcel in) {
		PREVIEW_VIDEO_WIDTH = in.readInt();
		PREVIEW_VIDEO_HEIGHT = in.readInt();
		mVideoWidth = in.readInt();
		mVideoHeight = in.readInt();
		mBitrate = in.readInt();
		MAX_CAPTURE_DURATION = in.readInt();
		MAX_CAPTURE_FILESIZE = in.readInt();

		OUTPUT_FORMAT = in.readInt();
		AUDIO_SOURCE = in.readInt();
		AUDIO_ENCODER = in.readInt();
		VIDEO_SOURCE = in.readInt();
		VIDEO_ENCODER = in.readInt();
	}

}