package com.jmolsmobile.landscapevideocapture;

import java.io.IOException;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.view.Surface;

/**
 * @author Jeroen Mols
 */
public class VideoRecorder {

	private final CaptureConfiguration		mCaptureConfiguration;
	private MediaRecorder					mRecorder;
	private final CaptureHelper				mHelper		= new CaptureHelper();
	boolean									mRecording	= false;
	private final VideoRecorderInterface	mRecorderInterface;
	private final VideoFile					mVideoFile;
	private final Surface					mPreviewSurface;
	private final Camera					mCamera;

	public VideoRecorder(CaptureConfiguration captureConfiguration, VideoRecorderInterface recorderInterface,
			VideoFile videoFile, Surface previewSurface, Camera camera) {
		mCaptureConfiguration = captureConfiguration;
		mRecorderInterface = recorderInterface;
		mVideoFile = videoFile;
		mPreviewSurface = previewSurface;
		mCamera = camera;
	}

	public CaptureConfiguration getCaptureConfiguration() {
		return mCaptureConfiguration;
	}

	boolean initRecorder() {
		final String outputFilename = mVideoFile.getFile().getAbsolutePath();
		final OnInfoListener recordingListener = new OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				switch (what) {
				case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
					// NOP
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
					CLog.d(CLog.ACTIVITY, "MediaRecorder max duration reached");
					stopRecording("Capture stopped - Max duration reached");
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
					CLog.d(CLog.ACTIVITY, "MediaRecorder max filesize reached");
					stopRecording("Capture stopped - Max file size reached");
					break;
				default:
					break;
				}
			}
		};

		try {
			mHelper.prepareCameraForRecording(mCamera);
		} catch (final PrepareCameraException e) {
			e.printStackTrace();
			mRecorderInterface.onRecordingFailed();
			return false;
		}

		mRecorder = mHelper.createMediaRecorder(mCamera, mPreviewSurface, mCaptureConfiguration, outputFilename,
				recordingListener);

		CLog.d(CLog.ACTIVITY, "MediaRecorder successfully initialized");
		return true;
	}

	public MediaRecorder getMediaRecorder() {
		return mRecorder;
	}

	boolean prepareRecorder() {
		try {
			mRecorder.prepare();
			CLog.d(CLog.ACTIVITY, "MediaRecorder successfully prepared");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.d(CLog.ACTIVITY, "MediaRecorder preparation failed - " + e.toString());
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			CLog.d(CLog.ACTIVITY, "MediaRecorder preparation failed - " + e.toString());
			return false;
		}
	}

	boolean startRecorder() {
		try {
			getMediaRecorder().start();
			CLog.d(CLog.ACTIVITY, "MediaRecorder successfully started");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.d(CLog.ACTIVITY, "MediaRecorder start failed - " + e.toString());
			return false;
		}
	}

	void releaseRecorderResources() {
		if (mRecorder != null) {
			mRecorder.release();
		}
	}

	public boolean isRecording() {
		return mRecording;
	}

	public void setRecording(boolean recording) {
		mRecording = recording;
	}

	public void stopRecording(String message) {
		try {
			getMediaRecorder().stop();
			mRecorderInterface.onRecordingSuccess();
		} catch (final RuntimeException e) {
			CLog.d(CLog.ACTIVITY, "Failed to stop recording");
		}

		mRecording = false;
		mRecorderInterface.onRecordingStopped(null);
	}

	public void startRecording() {
		setRecording(false);

		if (!initRecorder()) return;
		if (!prepareRecorder()) return;
		if (!startRecorder()) return;

		// Update UI
		setRecording(true);
		mRecorderInterface.onRecordingStarted();
		CLog.d(CLog.ACTIVITY, "Successfully started recording - outputfile: " + mVideoFile.getFile().getAbsolutePath());
	}

}