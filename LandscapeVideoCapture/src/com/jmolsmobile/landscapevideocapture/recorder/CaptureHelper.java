package com.jmolsmobile.landscapevideocapture.recorder;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException.OpenType;

/**
 * @author Jeroen Mols
 */
public class CaptureHelper {

	public Camera openCamera() throws OpenCameraException {
		Camera camera = null;
		try {
			camera = openCameraFromSystem();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new OpenCameraException(OpenType.INUSE);
		}

		if (camera == null) throw new OpenCameraException(OpenType.NOCAMERA);
		return camera;
	}

	public void prepareCameraForRecording(Camera camera) throws PrepareCameraException {
		try {
			unlockCameraFromSystem(camera);
		} catch (final RuntimeException e) {
			throw new PrepareCameraException();
		}
	}

	protected Camera openCameraFromSystem() {
		return Camera.open(CameraInfo.CAMERA_FACING_BACK);
	}

	protected void unlockCameraFromSystem(Camera camera) {
		camera.unlock();
	}

}
