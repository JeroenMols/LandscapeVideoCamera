package com.jmolsmobile.landscapevideocapture.recorder;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException.OpenType;

/**
 * @author Jeroen Mols
 */
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

}
