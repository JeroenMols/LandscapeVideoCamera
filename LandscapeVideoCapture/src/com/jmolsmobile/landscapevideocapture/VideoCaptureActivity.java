package com.jmolsmobile.landscapevideocapture;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
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

		final Surface previewSurface = mVideoCaptureView.getPreviewSurfaceHolder().getSurface();
		mVideoRecorder = new VideoRecorder(captureConfiguration, this, mVideoFile, previewSurface, mCamera);

		if (mVideoRecorded) {
			mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(mVideoFile.getFullPath()));
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
		savedInstanceState.putString(SAVED_OUTPUT_FILENAME, mVideoFile.getFullPath());
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

	@Override
	public void onRecordButtonClicked() {
		if (mVideoRecorder.isRecording()) {
			mVideoRecorder.stopRecording(null);
		} else {
			mVideoRecorder.startRecording();
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

		mVideoCaptureView.updateUIRecordingFinished(mHelper.generateThumbnail(mVideoFile.getFullPath()));
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

}
