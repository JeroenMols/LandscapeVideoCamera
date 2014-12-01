package com.jmolsmobile.landscapevideocapture.recorder;

import java.io.IOException;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException.OpenType;

/**
 * @author Jeroen Mols
 */
@SuppressWarnings("deprecation")
public class CameraWrapper {

	private Camera	mCamera	= null;

	public Camera getCamera() {
		return mCamera;
	}

	public void openCamera() throws OpenCameraException {
		mCamera = null;
		try {
			mCamera = openCameraFromSystem();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new OpenCameraException(OpenType.INUSE);
		}

		if (mCamera == null) throw new OpenCameraException(OpenType.NOCAMERA);
	}

	public void prepareCameraForRecording() throws PrepareCameraException {
		try {
			unlockCameraFromSystem();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new PrepareCameraException();
		}
	}

	public void releaseCamera() {
		if (mCamera == null) return;
		mCamera.release();
	}

	protected Camera openCameraFromSystem() {
		return Camera.open(CameraInfo.CAMERA_FACING_BACK);
	}

	protected void unlockCameraFromSystem() {
		mCamera.unlock();
	}

	public void startPreview(final SurfaceHolder holder) throws IOException {
		mCamera.setPreviewDisplay(holder);
		mCamera.startPreview();
	}

	public void stopPreview() throws Exception {
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
	}

	public void configureForPreview(int width, int height) {
		final Parameters params = mCamera.getParameters();
		params.setPreviewSize(width, height);
		params.setPreviewFormat(ImageFormat.NV21);
		mCamera.setParameters(params);
	}

}
