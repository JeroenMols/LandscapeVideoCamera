package com.jmolsmobile.landscapevideocapture.recorder;

import java.io.IOException;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.CLog;
import com.jmolsmobile.landscapevideocapture.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.VideoFile;
import com.jmolsmobile.landscapevideocapture.preview.CapturePreview;
import com.jmolsmobile.landscapevideocapture.preview.CapturePreviewInterface;

/**
 * @author Jeroen Mols
 */
public class VideoRecorder implements OnInfoListener, CapturePreviewInterface {

	private final CaptureHelper				mHelper		= new CaptureHelper();
	private Camera							mCamera;
	private final Surface					mPreviewSurface;
	private CapturePreview					mVideoCapturePreview;

	private final CaptureConfiguration		mCaptureConfiguration;
	private final VideoFile					mVideoFile;

	private MediaRecorder					mRecorder;
	private boolean							mRecording	= false;
	private final VideoRecorderInterface	mRecorderInterface;

	public VideoRecorder(VideoRecorderInterface recorderInterface, CaptureConfiguration captureConfiguration,
			VideoFile videoFile, SurfaceHolder previewHolder) {
		mCaptureConfiguration = captureConfiguration;
		mRecorderInterface = recorderInterface;
		mVideoFile = videoFile;
		mPreviewSurface = previewHolder.getSurface();

		initializeCameraAndPreview(previewHolder);
	}

	protected void initializeCameraAndPreview(SurfaceHolder previewHolder) {
		try {
			mCamera = mHelper.openCamera();
		} catch (final OpenCameraException e) {
			e.printStackTrace();
			mRecorderInterface.onRecordingFailed(e.getMessage());
			return;
		}

		final int width = mCaptureConfiguration.getPreviewWidth();
		final int height = mCaptureConfiguration.getPreviewHeight();
		mVideoCapturePreview = new CapturePreview(this, mCamera, previewHolder, width, height);
	}

	public void toggleRecording() {
		if (mRecording) {
			stopRecording(null);
		} else {
			startRecording();
		}
	}

	private void startRecording() {
		mRecording = false;

		if (!initRecorder()) return;
		if (!prepareRecorder()) return;
		if (!startRecorder()) return;

		mRecording = true;
		mRecorderInterface.onRecordingStarted();
		CLog.d(CLog.RECORDER, "Successfully started recording - outputfile: " + mVideoFile.getFullPath());
	}

	public void stopRecording(String message) {
		if (!mRecording) return;

		try {
			mRecorder.stop();
			mRecorderInterface.onRecordingSuccess();
			CLog.d(CLog.RECORDER, "Successfully stopped recording - outputfile: " + mVideoFile.getFullPath());
		} catch (final RuntimeException e) {
			CLog.d(CLog.RECORDER, "Failed to stop recording");
		}

		mRecording = false;
		mRecorderInterface.onRecordingStopped(null);
	}

	private boolean initRecorder() {
		try {
			mHelper.prepareCameraForRecording(mCamera);
		} catch (final PrepareCameraException e) {
			e.printStackTrace();
			mRecorderInterface.onRecordingFailed("Unable to record video");
			CLog.e(CLog.RECORDER, "Failed to initialize recorder - " + e.toString());
			return false;
		}

		mRecorder = createMediaRecorder();
		configureMediaRecorder(mRecorder, mCamera);

		CLog.d(CLog.RECORDER, "MediaRecorder successfully initialized");
		return true;
	}

	protected void configureMediaRecorder(final MediaRecorder recorder, Camera camera) throws IllegalStateException,
	IllegalArgumentException {
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

	protected MediaRecorder createMediaRecorder() {
		return new MediaRecorder();
	}

	private boolean prepareRecorder() {
		try {
			mRecorder.prepare();
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
			mRecorder.start();
			CLog.d(CLog.RECORDER, "MediaRecorder successfully started");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.e(CLog.RECORDER, "MediaRecorder start failed - " + e.toString());
			return false;
		}
	}

	private void releaseRecorderResources() {
		if (mRecorder != null) {
			mRecorder.release();
		}
	}

	public void releaseAllResources() {
		if (mVideoCapturePreview != null) {
			mVideoCapturePreview.releasePreviewResources();
		}
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		releaseRecorderResources();
		CLog.d(CLog.RECORDER, "Released all resources");
	}

	public Bitmap getVideoThumbnail() {
		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoFile.getFullPath(),
				Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail == null) {
			CLog.d(CLog.RECORDER, "Failed to generate video preview");
		}
		return thumbnail;
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