package com.jmolsmobile.landscapevideocapture;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author Jeroen Mols
 */
public class VideoCaptureActivity extends Activity implements RecordingButtonInterface {

	public static final int				RESULT_ERROR			= 753245;

	public static final String			EXTRA_OUTPUT_FILENAME	= "com.jmolsmobile.extraoutputfilename";
	public static final String			EXTRA_ERROR_MESSAGE		= "com.jmolsmobile.extraerrormessage";

	private static final String			SAVED_RECORDED_BOOLEAN	= "com.jmolsmobile.savedrecordedboolean";
	protected static final String		SAVED_OUTPUT_FILENAME	= "com.jmolsmobile.savedoutputfilename";

	private VideoFile					mVideoFile				= null;
	private final CaptureHelper			mHelper					= new CaptureHelper();
	private VideoCaptureView			mVideoCaptureView;

	private MediaRecorder				mRecorder;
	private SurfaceHolder				mSurfaceHolder;

	boolean								mRecording				= false;

	private boolean						mVideoRecorded			= false;

	private Camera						mCamera;
	private final CaptureConfiguration	mCaptureConfiguration	= new CaptureConfiguration();

	private VideoCapturePreview			mVideoCapturePreview;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_videocapture);

		if (savedInstanceState != null) {
			mVideoRecorded = savedInstanceState.getBoolean(SAVED_RECORDED_BOOLEAN, false);
		}

		mVideoFile = generateOutputFile(savedInstanceState);

		mVideoCaptureView = (VideoCaptureView) findViewById(R.id.videocapture_videocaptureview_vcv);
		if (mVideoCaptureView == null) return; // Wrong orientation

		initializeCamera();
		mVideoCaptureView.setRecordingButtonInterface(this);

		if (mVideoRecorded) {
			mVideoCaptureView.updateUIRecordingFinished(generateThumbnail());
			return;
		}

		mVideoCapturePreview = new VideoCapturePreview(mCamera, mVideoCaptureView);
		mSurfaceHolder = mVideoCaptureView.getSurfaceView().getHolder();
		mSurfaceHolder.addCallback(mVideoCapturePreview);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Necessary for older API's

		mVideoCaptureView.updateUINotRecording();
	}

	@Override
	protected void onPause() {
		if (mRecording) {
			stopRecording();
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

	@Override
	public void onBackPressed() {
		finishCancelled();
	}

	// METHODS TO CONTROL THE RECORDING
	public boolean startRecording() {
		if (!initRecorder(mCaptureConfiguration)) return false;
		if (!prepareRecorder()) return false;
		if (!startRecorder()) return false;

		// Update UI
		mVideoCaptureView.updateUIRecordingOngoing();
		CLog.d(CLog.ACTIVITY, "Successfully started recording - outputfile: " + getOutputFilename());
		return true;
	}

	public boolean stopRecording() {
		try {
			mRecorder.stop();
			mVideoRecorded = true;
		} catch (final RuntimeException e) {
			CLog.d(CLog.ACTIVITY, "Failed to stop recording");
		}
		mRecording = false;

		mVideoCaptureView.updateUIRecordingFinished(generateThumbnail());
		releaseAllResources();
		return true;
	}

	// UTILITY METHODS
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

	private String getOutputFilename() {
		return mVideoFile.getFile().getAbsolutePath();
	}

	@Override
	public void onRecordButtonClicked() {
		if (mRecording) {
			stopRecording();
		} else {
			this.mRecording = startRecording();
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
		Toast.makeText(getApplicationContext(), "Can't capture video" + message, Toast.LENGTH_LONG).show();

		final Intent result = new Intent();
		result.putExtra(EXTRA_ERROR_MESSAGE, message);
		this.setResult(RESULT_ERROR, result);
		finish();
	}

	@SuppressWarnings("deprecation")
	private Bitmap generateThumbnail() {
		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(getOutputFilename(),
				MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail == null) {
			CLog.d(CLog.ACTIVITY, "Failed to generate video preview");
		}
		return thumbnail;
	}

	// METHODS TO CONTROL THE MEDIARECORDER
	private boolean initRecorder(CaptureConfiguration captureConfiguration) {
		final Camera camera = mCamera;
		final Surface previewSurface = mSurfaceHolder.getSurface();
		final String outputFilename = getOutputFilename();
		final OnInfoListener recordingListener = new OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				switch (what) {
				case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
					// NOP
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
					CLog.d(CLog.ACTIVITY, "MediaRecorder max duration reached");
					Toast.makeText(getApplicationContext(), "Capture stopped - Max duration reached", Toast.LENGTH_LONG)
							.show();
					stopRecording();
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
					CLog.d(CLog.ACTIVITY, "MediaRecorder max filesize reached");
					Toast.makeText(getApplicationContext(), "Capture stopped - Max file size reached",
							Toast.LENGTH_LONG).show();
					stopRecording();
					break;
				default:
					break;
				}
			}
		};

		try {
			mHelper.prepareCameraForRecording(camera);
		} catch (final PrepareCameraException e) {
			e.printStackTrace();
			finishError(e.getMessage());
			return false;
		}

		mRecorder = mHelper.createMediaRecorder(camera, previewSurface, captureConfiguration, outputFilename,
				recordingListener);

		CLog.d(CLog.ACTIVITY, "MediaRecorder successfully initialized");
		return true;
	}

	private boolean prepareRecorder() {
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

	private boolean startRecorder() {
		try {
			mRecorder.start();
			CLog.d(CLog.ACTIVITY, "MediaRecorder successfully started");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			CLog.d(CLog.ACTIVITY, "MediaRecorder start failed - " + e.toString());
			return false;
		}
	}

	private void releaseAllResources() {
		if (mVideoCapturePreview != null) {
			mVideoCapturePreview.releasePreviewResources();
		}

		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mRecorder != null) {
			mRecorder.release();
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

	/**
	 * @author Jeroen Mols
	 */
	class VideoCapturePreview implements SurfaceHolder.Callback {

		private boolean			mPreviewRunning	= false;
		private final Camera	mPreviewCamera;

		public VideoCapturePreview(Camera camera, VideoCaptureView videoCaptureView) {
			mPreviewCamera = camera;
		}

		@Override
		public void surfaceCreated(final SurfaceHolder holder) {
			// NOP
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
			if (mPreviewRunning) {
				mPreviewCamera.stopPreview();
			}

			final Camera.Parameters params = mPreviewCamera.getParameters();
			params.setPreviewSize(mCaptureConfiguration.getPreviewWidth(), mCaptureConfiguration.getPreviewHeight());
			params.setPreviewFormat(ImageFormat.NV21);

			try {
				mPreviewCamera.setParameters(params);
			} catch (final RuntimeException e) {
				e.printStackTrace();
				CLog.d(CLog.ACTIVITY, "Failed to show preview - invalid parameters set to camera preview");
				Toast.makeText(getApplicationContext(), "Can't capture video - Unable to show camera preview",
						Toast.LENGTH_LONG).show();
				finishError("Invalid parameters set to camera preview");
			}

			try {
				mPreviewCamera.setPreviewDisplay(holder);
				mPreviewCamera.startPreview();
				mPreviewRunning = true;
			} catch (final IOException e) {
				e.printStackTrace();
				CLog.d(CLog.ACTIVITY, "Failed to show preview - unable to connect camera to preview");
				Toast.makeText(getApplicationContext(), "Can't capture video - Unable to show camera preview",
						Toast.LENGTH_LONG).show();
				finishError("Invalid parameters set to camera preview");
			} catch (final RuntimeException e) {
				e.printStackTrace();
				CLog.d(CLog.ACTIVITY, "Failed to show preview - unable to start camera preview");
				Toast.makeText(getApplicationContext(), "Unable to show camera preview", Toast.LENGTH_LONG).show();
				// finishError("Invalid parameters set to camera preview");
			}
		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			// NOP
		}

		public void releasePreviewResources() {
			if (mPreviewRunning) {
				try {
					mPreviewCamera.stopPreview();
					mPreviewCamera.setPreviewCallback(null);
					this.mPreviewRunning = false;
				} catch (final Exception e) {
					// TODO implement
				}
			}
		}
	}

}
