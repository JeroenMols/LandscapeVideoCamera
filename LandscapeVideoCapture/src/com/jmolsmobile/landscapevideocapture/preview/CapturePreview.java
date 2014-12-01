package com.jmolsmobile.landscapevideocapture.preview;

import java.io.IOException;

import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.CLog;
import com.jmolsmobile.landscapevideocapture.recorder.CameraWrapper;

/**
 * @author Jeroen Mols
 */
public class CapturePreview implements SurfaceHolder.Callback {

	private boolean							mPreviewRunning	= false;
	private final CapturePreviewInterface	mInterface;
	private final int						mPreviewWidth;
	private final int						mPreviewHeight;
	public final CameraWrapper				mCameraWrapper;

	public CapturePreview(CapturePreviewInterface capturePreviewInterface, CameraWrapper cameraWrapper, SurfaceHolder holder,
			int width, int height) {
		mInterface = capturePreviewInterface;
		mCameraWrapper = cameraWrapper;
		mPreviewWidth = width;
		mPreviewHeight = height;

		initalizeSurfaceHolder(holder);
	}

	@SuppressWarnings("deprecation")
	private void initalizeSurfaceHolder(final SurfaceHolder surfaceHolder) {
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Necessary for older API's
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		// NOP
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		if (mPreviewRunning) {
			try {
				mCameraWrapper.stopPreview();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		try {
			mCameraWrapper.configureForPreview(mPreviewWidth, mPreviewHeight);
		} catch (final RuntimeException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - invalid parameters set to camera preview");
			mInterface.onCapturePreviewFailed();
			return;
		}

		try {
			mCameraWrapper.startPreview(holder);
			setPreviewRunning(true);
		} catch (final IOException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - unable to connect camera to preview (IOException)");
			mInterface.onCapturePreviewFailed();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - unable to start camera preview (RuntimeException)");
			mInterface.onCapturePreviewFailed();
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		// NOP
	}

	public void releasePreviewResources() {
		if (mPreviewRunning) {
			try {
				mCameraWrapper.stopPreview();
				setPreviewRunning(false);
			} catch (final Exception e) {
				e.printStackTrace();
				CLog.e(CLog.PREVIEW, "Failed to clean up preview resources");
			}
		}
	}

	protected void setPreviewRunning(boolean running) {
		mPreviewRunning = running;
	}

}