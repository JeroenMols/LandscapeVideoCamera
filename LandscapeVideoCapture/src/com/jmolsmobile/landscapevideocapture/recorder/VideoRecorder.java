package com.jmolsmobile.landscapevideocapture.recorder;

import java.io.IOException;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.CLog;
import com.jmolsmobile.landscapevideocapture.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.VideoFile;
import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException;
import com.jmolsmobile.landscapevideocapture.camera.PrepareCameraException;
import com.jmolsmobile.landscapevideocapture.preview.CapturePreview;
import com.jmolsmobile.landscapevideocapture.preview.CapturePreviewInterface;

/**
 * @author Jeroen Mols
 */
public class VideoRecorder implements OnInfoListener, CapturePreviewInterface {

	private CameraWrapper					mCameraWrapper;
	private final Surface					mPreviewSurface;
	private CapturePreview					mVideoCapturePreview;

	private final CaptureConfiguration		mCaptureConfiguration;
	private final VideoFile					mVideoFile;

	private MediaRecorder					mRecorder;
	private boolean							mRecording	= false;
	private final VideoRecorderInterface	mRecorderInterface;

	public VideoRecorder(VideoRecorderInterface recorderInterface, CaptureConfiguration captureConfiguration,
			VideoFile videoFile, CameraWrapper cameraWrapper, SurfaceHolder previewHolder) {
		mCaptureConfiguration = captureConfiguration;
		mRecorderInterface = recorderInterface;
		mVideoFile = videoFile;
		mCameraWrapper = cameraWrapper;
		mPreviewSurface = previewHolder.getSurface();

		initializeCameraAndPreview(previewHolder);
	}

	protected void initializeCameraAndPreview(SurfaceHolder previewHolder) {
		try {
			mCameraWrapper.openCamera();
		} catch (final OpenCameraException e) {
			e.printStackTrace();
			mRecorderInterface.onRecordingFailed(e.getMessage());
			return;
		}

		final int width = mCaptureConfiguration.getPreviewWidth();
		final int height = mCaptureConfiguration.getPreviewHeight();
		mVideoCapturePreview = new CapturePreview(this, mCameraWrapper, previewHolder, width, height);
	}

	public void toggleRecording() {
		if (isRecording()) {
			stopRecording(null);
		} else {
			startRecording();
		}
	}

	protected void startRecording() {
		mRecording = false;

		if (!initRecorder()) return;
		if (!prepareRecorder()) return;
		if (!startRecorder()) return;

		mRecording = true;
		mRecorderInterface.onRecordingStarted();
		CLog.d(CLog.RECORDER, "Successfully started recording - outputfile: " + mVideoFile.getFullPath());
	}

	public void stopRecording(String message) {
		if (!isRecording()) return;

		try {
			getMediaRecorder().stop();
			mRecorderInterface.onRecordingSuccess();
			CLog.d(CLog.RECORDER, "Successfully stopped recording - outputfile: " + mVideoFile.getFullPath());
		} catch (final RuntimeException e) {
			CLog.d(CLog.RECORDER, "Failed to stop recording");
		}

		mRecording = false;
		mRecorderInterface.onRecordingStopped(message);
	}

	private boolean initRecorder() {
		try {
			mCameraWrapper.prepareCameraForRecording();
		} catch (final PrepareCameraException e) {
			e.printStackTrace();
			mRecorderInterface.onRecordingFailed("Unable to record video");
			CLog.e(CLog.RECORDER, "Failed to initialize recorder - " + e.toString());
			return false;
		}

		mRecorder = new MediaRecorder();
		configureMediaRecorder(getMediaRecorder(), mCameraWrapper.getCamera());

		CLog.d(CLog.RECORDER, "MediaRecorder successfully initialized");
		return true;
	}

	@SuppressWarnings("deprecation")
	protected void configureMediaRecorder(final MediaRecorder recorder, android.hardware.Camera camera)
			throws IllegalStateException, IllegalArgumentException {
		recorder.setCamera(camera);
		recorder.setAudioSource(mCaptureConfiguration.getAudioSource());
		recorder.setVideoSource(mCaptureConfiguration.getVideoSource());

		// Order is important
		recorder.setOutputFormat(mCaptureConfiguration.getOutputFormat());
		recorder.setMaxDuration(mCaptureConfiguration.getMaxCaptureDuration());
		recorder.setOutputFile(mVideoFile.getFullPath());

		recorder.setVideoSize(mCaptureConfiguration.getVideoWidth(), mCaptureConfiguration.getVideoHeight());
		recorder.setVideoEncodingBitRate(mCaptureConfiguration.getVideoBitrate());

		recorder.setAudioEncoder(mCaptureConfiguration.getAudioEncoder());
		recorder.setVideoEncoder(mCaptureConfiguration.getVideoEncoder());

		recorder.setPreviewDisplay(mPreviewSurface);
		recorder.setMaxFileSize(mCaptureConfiguration.getMaxCaptureFileSize());
		recorder.setOnInfoListener(this);
	}

	private boolean prepareRecorder() {
		try {
			getMediaRecorder().prepare();
			CLog.d(CLog.RECORDER, "MediaRecorder successfully prepared");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.e(CLog.RECORDER, "MediaRecorder preparation failed - " + e.toString());
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			CLog.e(CLog.RECORDER, "MediaRecorder preparation failed - " + e.toString());
			return false;
		}
	}

	private boolean startRecorder() {
		try {
			getMediaRecorder().start();
			CLog.d(CLog.RECORDER, "MediaRecorder successfully started");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.e(CLog.RECORDER, "MediaRecorder start failed - " + e.toString());
			return false;
		}
	}

	protected boolean isRecording() {
		return mRecording;
	}

	protected MediaRecorder getMediaRecorder() {
		return mRecorder;
	}

	private void releaseRecorderResources() {
		if (getMediaRecorder() != null) {
			getMediaRecorder().release();
		}
	}

	public void releaseAllResources() {
		if (mVideoCapturePreview != null) {
			mVideoCapturePreview.releasePreviewResources();
		}
		if (mCameraWrapper != null) {
			mCameraWrapper.releaseCamera();
			mCameraWrapper = null;
		}
		releaseRecorderResources();
		CLog.d(CLog.RECORDER, "Released all resources");
	}

	@Override
	public void onCapturePreviewFailed() {
		mRecorderInterface.onRecordingFailed("Unable to show camera preview");
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
			// NOP
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
			CLog.d(CLog.RECORDER, "MediaRecorder max duration reached");
			stopRecording("Capture stopped - Max duration reached");
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			CLog.d(CLog.RECORDER, "MediaRecorder max filesize reached");
			stopRecording("Capture stopped - Max file size reached");
			break;
		default:
			break;
		}
	}

}