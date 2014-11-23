package com.jmolsmobile.landscapevideocapture;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author Jeroen Mols
 * @date 03 Jan 2014
 */
public class VideoCaptureActivity extends Activity {

	public static final int			RESULT_ERROR			= 753245;

	public static final String		EXTRA_OUTPUT_FILENAME	= "com.jmolsmobile.extraoutputfilename";
	public static final String		EXTRA_ERROR_MESSAGE		= "com.jmolsmobile.extraerrormessage";

	private static final String		LOG_CAPTURE_TAG			= "VideoCapture";
	private static final String		SAVED_RECORDED_BOOLEAN	= "com.jmolsmobile.savedrecordedboolean";
	protected static final String	SAVED_OUTPUT_FILENAME	= "com.jmolsmobile.savedoutputfilename";

	private VideoFile				mVideoFile				= null;

	private MediaRecorder			mRecorder;
	private SurfaceHolder			mSurfaceHolder;

	private boolean					mRecording				= false;
	private boolean					mVideoRecorded			= false;
	private boolean					mPreviewRunning			= false;

	private Camera					mCamera;
	private SurfaceView				mSurfaceView;
	private ImageView				mThumbnailIv;
	private ImageView				mRecordBtnIv;
	private ImageView				mAcceptBtnIv;
	private ImageView				mDeclineBtnIv;

	// ADJUST THESE TO YOUR NEEDS
	private static final int		PREVIEW_VIDEO_WIDTH		= 640;
	private static final int		PREVIEW_VIDEO_HEIGHT	= 480;
	private static final int		CAPTURE_VIDEO_WIDTH		= 640;
	private static final int		CAPTURE_VIDEO_HEIGHT	= 480;
	private static final int		FRAMES_PER_SECOND		= 25;
	private static final int		BITRATE_PER_SECOND		= 750000;									// bit per sec
	private static final int		MAX_CAPTURE_DURATION	= 30000;									// in ms
	private static final int		MAX_CAPTURE_FILESIZE	= 10;										// in mb

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

		mSurfaceView = (SurfaceView) findViewById(R.id.videocapture_preview_sv);
		if (mSurfaceView == null) return; // Wrong orientation

		mThumbnailIv = (ImageView) findViewById(R.id.videocapture_preview_iv);
		initializeAllButtons();

		if (mVideoRecorded) {
			updateUIRecordingFinished();
			return;
		}

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new SurfaceCallbackHandler());
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Necessary for older API's

		updateUINotRecording();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	private boolean startRecording() {
		if (!initRecorder()) return false;
		if (!prepareRecorder()) return false;
		if (!startRecorder()) return false;

		// Update UI
		updateUIRecordingOngoing();
		Log.d(LOG_CAPTURE_TAG, "Successfully started recording - outputfile: " + getOutputFilename());
		return true;
	}

	private boolean stopRecording() {
		try {
			mRecorder.stop();
			mVideoRecorded = true;
		} catch (final RuntimeException e) {
			Log.d(LOG_CAPTURE_TAG, "Failed to stop recording");
		}
		mRecording = false;

		updateUIRecordingFinished();
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

	private void finishCompleted(final String filename) {
		final Intent result = new Intent();
		result.putExtra(EXTRA_OUTPUT_FILENAME, filename);
		this.setResult(RESULT_OK, result);
		finish();
	}

	private void finishCancelled() {
		this.setResult(RESULT_CANCELED);
		finish();
	}

	private void finishError(final String message) {
		final Intent result = new Intent();
		result.putExtra(EXTRA_ERROR_MESSAGE, message);
		this.setResult(RESULT_ERROR, result);
		finish();
	}

	// METHODS TO UPDATE UI
	private void updateUINotRecording() {
		mRecordBtnIv.setSelected(false);
		mRecordBtnIv.setVisibility(View.VISIBLE);
		mAcceptBtnIv.setVisibility(View.GONE);
		mDeclineBtnIv.setVisibility(View.GONE);
		mThumbnailIv.setVisibility(View.GONE);
		mSurfaceView.setVisibility(View.VISIBLE);
	}

	private void updateUIRecordingOngoing() {
		mRecordBtnIv.setSelected(true);
		mRecordBtnIv.setVisibility(View.VISIBLE);
		mAcceptBtnIv.setVisibility(View.GONE);
		mDeclineBtnIv.setVisibility(View.GONE);
		mThumbnailIv.setVisibility(View.GONE);
		mSurfaceView.setVisibility(View.VISIBLE);
	}

	private void updateUIRecordingFinished() {
		mRecordBtnIv.setVisibility(View.INVISIBLE);
		mAcceptBtnIv.setVisibility(View.VISIBLE);
		mDeclineBtnIv.setVisibility(View.VISIBLE);
		mThumbnailIv.setVisibility(View.VISIBLE);
		mSurfaceView.setVisibility(View.GONE);
		generateThumbnail();
	}

	private void initializeAllButtons() {
		mRecordBtnIv = (ImageView) findViewById(R.id.videocapture_recordbtn_iv);
		mAcceptBtnIv = (ImageView) findViewById(R.id.videocapture_acceptbtn_iv);
		mDeclineBtnIv = (ImageView) findViewById(R.id.videocapture_declinebtn_iv);

		mRecordBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (mRecording) {
					stopRecording();
				} else {
					mRecording = startRecording();
				}
			}
		});

		mAcceptBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				finishCompleted(getOutputFilename());
			}
		});
		mDeclineBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				finishCancelled();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void generateThumbnail() {
		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(getOutputFilename(),
				MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail == null) {
			Log.d(LOG_CAPTURE_TAG, "Failed to generate video preview");
			return;
		}

		mThumbnailIv.setBackgroundDrawable(new BitmapDrawable(thumbnail));
	}

	// METHODS TO CONTROL THE MEDIARECORDER
	private boolean initRecorder() {
		if (mCamera == null) {
			mCamera = openCamera();
		}

		try {
			mCamera.unlock();
		} catch (final NullPointerException e) {
			Toast.makeText(this, "Can't start capture - device doesn't have a camera", Toast.LENGTH_LONG).show();
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder initialization failed - Device does not have a camera");
			return false;
		} catch (final RuntimeException e) {
			Toast.makeText(this, "Can't start capture - camera is used by another process", Toast.LENGTH_LONG).show();
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder initialization failed - Camera in use by another process");
			return false;
		}

		mRecorder = new MediaRecorder();
		mRecorder.setCamera(mCamera);
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Order is important
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setMaxDuration(MAX_CAPTURE_DURATION);
		mRecorder.setOutputFile(getOutputFilename());

		// Setting framerate explicitely is not supported on every Android device - use with caution
		// mRecorder.setVideoFrameRate(FRAMES_PER_SECOND);
		mRecorder.setVideoSize(CAPTURE_VIDEO_WIDTH, CAPTURE_VIDEO_HEIGHT);
		mRecorder.setVideoEncodingBitRate(BITRATE_PER_SECOND);

		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

		mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

		mRecorder.setMaxFileSize(MAX_CAPTURE_FILESIZE * 1024 * 1024);

		mRecorder.setOnInfoListener(new OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				switch (what) {
				case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
					// NOP
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
					Log.d(LOG_CAPTURE_TAG, "MediaRecorder max duration reached");
					if (mRecording) {
						Toast.makeText(getApplicationContext(), "Capture stopped - Max duration reached",
								Toast.LENGTH_LONG).show();
						stopRecording();
					}
					break;
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
					Log.d(LOG_CAPTURE_TAG, "MediaRecorder max filesize reached");
					if (mRecording) {
						Toast.makeText(getApplicationContext(), "Capture stopped - Max file size reached",
								Toast.LENGTH_LONG).show();
						stopRecording();
					}
					break;
				default:
					break;
				}
			}
		});

		Log.d(LOG_CAPTURE_TAG, "MediaRecorder successfully initialized");
		return true;
	}

	private boolean prepareRecorder() {
		try {
			mRecorder.prepare();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder successfully prepared");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder preparation failed - " + e.toString());
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder preparation failed - " + e.toString());
			return false;
		}
	}

	private boolean startRecorder() {
		try {
			mRecorder.start();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder successfully started");
			return true;
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "MediaRecorder start failed - " + e.toString());
			return false;
		}
	}

	private void releaseAllResources() {
		if (mPreviewRunning) {
			try {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mPreviewRunning = false;
			} catch (final Exception e) {
			}
		}

		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mRecorder != null) {
			mRecorder.release();
		}
		Log.d(LOG_CAPTURE_TAG, "Released all resources");
	}

	private Camera openCamera() {
		try {
			return Camera.open(CameraInfo.CAMERA_FACING_BACK);
		} catch (final Exception e) {
			e.printStackTrace();
			Log.d(LOG_CAPTURE_TAG, "Unable to open camera - Disabled or in use by other process");
			Toast.makeText(getApplicationContext(), "Can't capture video - Unable to connect to camera",
					Toast.LENGTH_LONG).show();
			finishError("Camera in use by other process");
		}
		return null;
	}

	/**
	 * Nested class to control the video preview
	 * 
	 * @author Jeroen Mols
	 * @date 04/02/2014
	 */
	private class SurfaceCallbackHandler implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(final SurfaceHolder holder) {

			final Camera camera = openCamera();

			if (camera == null) {
				Log.d(LOG_CAPTURE_TAG, "Failed to show preview - device doesn't have a camera");
				Toast.makeText(VideoCaptureActivity.this, "Can't capture video - device doesn't have a camera",
						Toast.LENGTH_LONG).show();
				finishError("Device does not have a camera");
				return;
			}

			mCamera = camera;
		}

		@Override
		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
			if (mPreviewRunning) {
				mCamera.stopPreview();
			}

			final Camera.Parameters params = mCamera.getParameters();
			params.setPreviewSize(PREVIEW_VIDEO_WIDTH, PREVIEW_VIDEO_HEIGHT);
			params.setPreviewFormat(ImageFormat.NV21);

			try {
				mCamera.setParameters(params);
			} catch (final RuntimeException e) {
				e.printStackTrace();
				Log.d(LOG_CAPTURE_TAG, "Failed to show preview - invalid parameters set to camera preview");
				Toast.makeText(getApplicationContext(), "Can't capture video - Unable to show camera preview",
						Toast.LENGTH_LONG).show();
				finishError("Invalid parameters set to camera preview");
			}

			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
				mPreviewRunning = true;
			} catch (final IOException e) {
				e.printStackTrace();
				Log.d(LOG_CAPTURE_TAG, "Failed to show preview - unable to connect camera to preview");
				Toast.makeText(getApplicationContext(), "Can't capture video - Unable to show camera preview",
						Toast.LENGTH_LONG).show();
				finishError("Invalid parameters set to camera preview");
			} catch (final RuntimeException e) {
				e.printStackTrace();
				Log.d(LOG_CAPTURE_TAG, "Failed to show preview - unable to start camera preview");
				Toast.makeText(getApplicationContext(), "Unable to show camera preview", Toast.LENGTH_LONG).show();
				// finishError("Invalid parameters set to camera preview");
			}
		}

		@Override
		public void surfaceDestroyed(final SurfaceHolder holder) {
			if (mRecording) {
				try {
					mRecorder.stop();
					Log.d(LOG_CAPTURE_TAG, "Successfully stopped ongoing recording");
				} catch (final IllegalStateException e) {
					e.printStackTrace();
					Log.d(LOG_CAPTURE_TAG, "Failed to stop ongoing recording - " + e.toString());
				}
				mRecording = false;
			}
			releaseAllResources();
			Log.d(LOG_CAPTURE_TAG, "Surface Destroyed - Released recoder");
		}
	}

}
