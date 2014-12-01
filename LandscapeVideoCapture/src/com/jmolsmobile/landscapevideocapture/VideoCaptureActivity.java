package com.jmolsmobile.landscapevideocapture;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author Jeroen Mols
 */
public class VideoCaptureActivity extends Activity implements RecordingButtonInterface, VideoRecorderInterface {

	public static final int			RESULT_ERROR			= 753245;

	public static final String		EXTRA_OUTPUT_FILENAME	= "com.jmolsmobile.extraoutputfilename";
	public static final String		EXTRA_ERROR_MESSAGE		= "com.jmolsmobile.extraerrormessage";

	private static final String		SAVED_RECORDED_BOOLEAN	= "com.jmolsmobile.savedrecordedboolean";
	protected static final String	SAVED_OUTPUT_FILENAME	= "com.jmolsmobile.savedoutputfilename";

	private VideoFile				mVideoFile				= null;
	private final CaptureHelper		mHelper					= new CaptureHelper();
	private VideoCaptureView		mVideoCaptureView;

	private VideoRecorder			mVideoRecorder;

	private boolean					mVideoRecorded			= false;

	Camera							mCamera;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initializeFields(savedInstanceState);

		setContentView(R.layout.activity_videocapture);
		final CaptureConfiguration captureConfiguration = new CaptureConfiguration();

		mVideoCaptureView = (VideoCaptureView) findViewById(R.id.videocapture_videocaptureview_vcv);
		if (mVideoCaptureView == null) return; // Wrong orientation

		initializeCamera();
		mVideoCaptureView.setRecordingButtonInterface(this);

		final Surface previewSurface = mVideoCaptureView.getPreviewSurfaceHolder().getSurface();
		mVideoRecorder = new VideoRecorder(captureConfiguration, this, mVideoFile, mCamera, mVideoCaptureView.getPreviewSurfaceHolder());

		if (mVideoRecorded) {
			mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(mVideoFile.getFullPath()));
			return;
		}

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
		savedInstanceState.putBoolean(SAVED_RECORDED_BOOLEAN, mVideoRecorded);
		savedInstanceState.putString(SAVED_OUTPUT_FILENAME, mVideoFile.getFullPath());
		super.onSaveInstanceState(savedInstanceState);
	}

	private void initializeFields(final Bundle savedInstanceState) {
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

	@Override
	public void onRecordButtonClicked() {
		mVideoRecorder.toggleRecording();
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
	public void onRecordingStarted() {
		mVideoCaptureView.updateUIRecordingOngoing();
	}

	@Override
	public void onRecordingStopped(String message) {
		if (message != null) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}

		mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(mVideoFile.getFullPath()));
		releaseAllResources();
	}

	@Override
	public void onRecordingSuccess() {
		mVideoRecorded = true;
	}

	@Override
	public void onRecordingFailed(String message) {
		finishError(message);
	}

	private void finishCompleted() {
		final Intent result = new Intent();
		result.putExtra(EXTRA_OUTPUT_FILENAME, mVideoFile.getFullPath());
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

	private void releaseAllResources() {
		if (mVideoRecorder != null) {
			if (mVideoRecorder.getVideoCapturePreview() != null) {
				mVideoRecorder.getVideoCapturePreview().releasePreviewResources();
			}
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
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

}
