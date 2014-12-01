package com.jmolsmobile.landscapevideocapture.recorder;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import android.hardware.Camera;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException.OpenType;

public class CaptureWrapperTest extends MockitoTestCase {

	public void test_openCameraSuccess() {
		final CameraWrapper spyHelper = spy(new CameraWrapper());
		final Camera mockCamera = mock(Camera.class);
		doReturn(mockCamera).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			final Camera camera = spyHelper.getCamera();
			assertEquals(mockCamera, camera);
		} catch (final OpenCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_openCameraNoCamera() {
		final CameraWrapper spyHelper = spy(new CameraWrapper());
		doReturn(null).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
		}
	}

	public void test_openCameraInUse() {
		final CameraWrapper spyHelper = spy(new CameraWrapper());
		doThrow(new RuntimeException()).when(spyHelper).openCameraFromSystem();

		try {
			spyHelper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
		}
	}

	public void test_prepareCameraShouldCallUnlock() {
		final CameraWrapper spyHelper = spy(new CameraWrapper());
		final Camera mockCamera = mock(Camera.class);
		doNothing().when(spyHelper).unlockCameraFromSystem();

		try {
			spyHelper.prepareCameraForRecording();
			verify(spyHelper, times(1)).unlockCameraFromSystem();
		} catch (final PrepareCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_prepareCameraWhenRuntimeException() {
		final CameraWrapper spyHelper = spy(new CameraWrapper());
		final Camera mockCamera = mock(Camera.class);
		doThrow(new RuntimeException()).when(spyHelper).unlockCameraFromSystem();

		try {
			spyHelper.prepareCameraForRecording();
			fail("Missing exception");
		} catch (final PrepareCameraException e) {
			assertEquals("Unable to use camera for recording", e.getMessage());
		}
	}

	public void test_releaseCameraWhenCameraNull() {
		final CameraWrapper wrapper = new CameraWrapper();
		wrapper.releaseCamera();
	}

	public void test_prepareCameraWhenCameraNull() {
		final CameraWrapper wrapper = new CameraWrapper();

		try {
			wrapper.prepareCameraForRecording();
			fail("Missing exception");
		} catch (final PrepareCameraException e) {
			assertEquals("Unable to use camera for recording", e.getMessage());
		}
	}
}
