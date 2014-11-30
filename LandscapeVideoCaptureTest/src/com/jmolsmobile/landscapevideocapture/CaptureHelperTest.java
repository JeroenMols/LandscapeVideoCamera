package com.jmolsmobile.landscapevideocapture;

import org.mockito.Mockito;

import android.hardware.Camera;

import com.jmolsmobile.landscapevideocapture.CameraException.OpenType;

public class CaptureHelperTest extends MockitoTestCase {

	public void test_openCameraSuccess() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final Camera mockCamera = Mockito.mock(Camera.class);
		Mockito.doReturn(mockCamera).when(spyHelper).openCameraFromSystem();

		try {
			final Camera camera = spyHelper.openCamera();
			assertEquals(mockCamera, camera);
		} catch (final CameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_openCameraNoCamera() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		Mockito.doReturn(null).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final CameraException e) {
			assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
		}
	}

	public void test_openCameraInUse() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		Mockito.doThrow(new RuntimeException()).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final CameraException e) {
			assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
		}
	}

	public void test_unlockCameraNull() {
		final CaptureHelper helper = new CaptureHelper();

		try {
			helper.prepareCameraForRecording(null);
			fail("Missing exception");
		} catch (final CameraException e) {
			assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
		}
	}
}
