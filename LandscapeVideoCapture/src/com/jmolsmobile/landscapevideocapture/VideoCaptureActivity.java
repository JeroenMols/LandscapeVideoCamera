package com.jmolsmobile.landscapevideocapture;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author Jeroen Mols
 */
public class VideoCaptureActivity extends Activity implements RecordingButtonInterface, CapturePreviewInterface,
VideoRecorderInterface {

	public static final int			RESULT_ERROR			= 753245;

	public static final String		EXTRA_OUTPUT_FILENAME	= "com.jmolsmobile.extraoutputfilename";
	public static final String		EXTRA_ERROR_MESSAGE		= "com.jmolsmobile.extraerrormessage";

	private static final String		SAVED_RECORDED_BOOLEAN	= "com.jmolsmobile.savedrecordedboolean";
	protected static final String	SAVED_OUTPUT_FILENAME	= "com.jmolsmobile.savedoutputfilename";

	private VideoFile				mVideoFile				= null;
	private final CaptureHelper		mHelper					= new CaptureHelper();
	private VideoCaptureView		mVideoCaptureView;
	private CapturePreview			mVideoCapturePreview;
	VideoRecorder					mVideoRecorder;

	private boolean					mVideoRecorded			= false;

	private Camera					mCamera;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		restoreInstanceState(savedInstanceState);

		setContentView(R.layout.activity_videocapture);
		final CaptureConfiguration captureConfiguration = new CaptureConfiguration();

		mVideoCaptureView = (VideoCaptureView) findViewById(R.id.videocapture_videocaptureview_vcv);
		if (mVideoCaptureView == null) return; // Wrong orientation

		initializeCamera();
		mVideoCaptureView.setRecordingButtonInterface(this);

		mVideoRecorder = new VideoRecorder(captureConfiguration, this, mVideoFile, null, mCamera);

		if (mVideoRecorded) {
			mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(getOutputFilename()));
			return;
		}

		final SurfaceHolder surfaceHolder = mVideoCaptureView.getPreviewSurfaceHolder();
		final int width = mVideoRecorder.getCaptureConfiguration().getPreviewWidth();
		final int height = mVideoRecorder.getCaptureConfiguration().getPreviewHeight();
		mVideoCapturePreview = new CapturePreview(this, mCamera, surfaceHolder, width, height);

		mVideoCaptureView.updateUINotRecording();
	}

	@Override
	protected void onPause() {
		if (mVideoRecorder != null && mVideoRecorder.isRecording()) {
			mVideoRecorder.stopRecording(null);
		}
		releaseAllResources();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(SAVED_RECORDED_BOOLEAN, mVideoRecorded);
		savedInstanceState.putString(SAVED_OUTPUT_FILENAME, getOutputFilename());
	}

	// TODO clean up restore methods
	private void restoreInstanceState(final Bundle savedInstanceState) {
		mVideoRecorded = generateVideoRecorded(savedInstanceState);
		mVideoFile = generateOutputFile(savedInstanceState);
	}

	private boolean generateVideoRecorded(final Bundle savedInstanceState) {
		if (savedInstanceState == null) return false;
		return savedInstanceState.getBoolean(SAVED_RECORDED_BOOLEAN, false);
	}

	protected VideoFile generateOutputFile(Bundle savedInstanceState) {
		VideoFile returnFile = null;
		if (savedInstanceState != null) {
			returnFile = new VideoFile(savedInstanceState.getString(SAVED_OUTPUT_FILENAME));
		} else {
			returnFile = new VideoFile(this.getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME));
		}

		// TODO: add checks to see if outputfile is writeable
		return returnFile;
	}

	@Override
	public void onBackPressed() {
		finishCancelled();
	}

	private String getOutputFilename() {
		return mVideoFile.getFile().getAbsolutePath();
	}

	@Override
	public void onRecordButtonClicked() {
		if (mVideoRecorder.isRecording()) {
			mVideoRecorder.stopRecording(null);
		} else {
			startRecording(this);
		}
	}

	@Override
	public void onAcceptButtonClicked() {
		finishCompleted();
	}

	@Override
	public void onDeclineButtonClicked() {
		finishCancelled();
	}

	@Override
	public void onCapturePreviewFailed() {
		finishError("Unable to show camera preview");
	}

	@Override
	public void onRecordingStarted() {
		mVideoCaptureView.updateUIRecordingOngoing();
	}

	@Override
	public void onRecordingStopped(String message) {
		if (message != null) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}

		mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(getOutputFilename()));
		releaseAllResources();
	}

	@Override
	public void onRecordingSuccess() {
		mVideoRecorded = true;
	}

	@Override
	public void onRecordingFailed() {
		finishError("Unable to record video");
	}

	private void finishCompleted() {
		final Intent result = new Intent();
		result.putExtra(EXTRA_OUTPUT_FILENAME, getOutputFilename());
		this.setResult(RESULT_OK, result);
		finish();
	}

	private void finishCancelled() {
		this.setResult(RESULT_CANCELED);
		finish();
	}

	private void finishError(final String message) {
		Toast.makeText(getApplicationContext(), "Can't capture video: " + message, Toast.LENGTH_LONG).show();

		final Intent result = new Intent();
		result.putExtra(EXTRA_ERROR_MESSAGE, message);
		this.setResult(RESULT_ERROR, result);
		finish();
	}

	// METHODS TO CONTROL THE RECORDING
	public void startRecording(VideoRecorderInterface recorderInterface) {
		mVideoRecorder.setRecording(false);

		if (!mVideoRecorder.initRecorder()) return;
		if (!mVideoRecorder.prepareRecorder()) return;
		if (!mVideoRecorder.startRecorder()) return;

		// Update UI
		mVideoRecorder.setRecording(true);
		recorderInterface.onRecordingStarted();
		CLog.d(CLog.ACTIVITY, "Successfully started recording - outputfile: " + getOutputFilename());
	}

	private void releaseAllResources() {
		if (mVideoCapturePreview != null) {
			mVideoCapturePreview.releasePreviewResources();
		}

		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mVideoRecorder != null) {
			mVideoRecorder.releaseRecorderResources();
		}
		CLog.d(CLog.ACTIVITY, "Released all resources");
	}

	private void initializeCamera() {
		try {
			mCamera = mHelper.openCamera();
		} catch (final OpenCameraException e) {
			e.printStackTrace();
			finishError(e.getMessage());
		}
	}

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

	}

}
