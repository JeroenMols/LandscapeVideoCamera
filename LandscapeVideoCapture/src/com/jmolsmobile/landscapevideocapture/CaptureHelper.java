package com.jmolsmobile.landscapevideocapture;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

import com.jmolsmobile.landscapevideocapture.CameraException.OpenType;

/**
 * @author Jeroen Mols
 */
public class CaptureHelper {

	public Camera openCamera() throws CameraException {
		Camera camera = null;
		try {
			camera = openCameraFromSystem();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new CameraException(OpenType.INUSE);
		}

		if (camera == null) throw new CameraException(OpenType.NOCAMERA);
		return camera;
	}

	public Camera openCameraFromSystem() {
		return Camera.open(CameraInfo.CAMERA_FACING_BACK);
	}

	public void prepareCameraForRecording(Camera camera) throws CameraException {
		throw new CameraException(OpenType.NOCAMERA);
	}

}
