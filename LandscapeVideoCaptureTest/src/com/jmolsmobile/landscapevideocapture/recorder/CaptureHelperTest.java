package com.jmolsmobile.landscapevideocapture.recorder;

import org.mockito.Mockito;

import android.hardware.Camera;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException.OpenType;

public class CaptureHelperTest extends MockitoTestCase {

	public void test_openCameraSuccess() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final Camera mockCamera = Mockito.mock(Camera.class);
		Mockito.doReturn(mockCamera).when(spyHelper).openCameraFromSystem();

		try {
			final Camera camera = spyHelper.openCamera();
			assertEquals(mockCamera, camera);
		} catch (final OpenCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_openCameraNoCamera() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		Mockito.doReturn(null).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
		}
	}

	public void test_openCameraInUse() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		Mockito.doThrow(new RuntimeException()).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
		}
	}

	public void test_prepareCameraShouldCallUnlock() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final Camera mockCamera = Mockito.mock(Camera.class);
		Mockito.doNothing().when(spyHelper).unlockCameraFromSystem(mockCamera);

		try {
			spyHelper.prepareCameraForRecording(mockCamera);
			Mockito.verify(spyHelper, Mockito.times(1)).unlockCameraFromSystem(mockCamera);
		} catch (final PrepareCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_prepareCameraWhenRuntimeException() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final Camera mockCamera = Mockito.mock(Camera.class);
		Mockito.doThrow(new RuntimeException()).when(spyHelper).unlockCameraFromSystem(mockCamera);

		try {
			spyHelper.prepareCameraForRecording(mockCamera);
			fail("Missing exception");
		} catch (final PrepareCameraException e) {
			assertEquals("Unable to use camera for recording", e.getMessage());
		}
	}

}
