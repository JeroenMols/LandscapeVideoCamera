package com.jmolsmobile.landscapevideocapture.camera;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import android.hardware.Camera;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;

/**
 * @author Jeroen Mols
 */
@SuppressWarnings("deprecation")
public class CameraWrapperTest extends MockitoTestCase {

	public void test_openCameraSuccess() {
		final CameraWrapper spyWrapper = spy(new CameraWrapper());
		final Camera mockCamera = mock(Camera.class);
		doReturn(mockCamera).when(spyWrapper).openCameraFromSystem();

		try {
			spyWrapper.openCamera();
			final Camera camera = spyWrapper.getCamera();
			assertEquals(mockCamera, camera);
		} catch (final OpenCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_openCameraNoCamera() {
		final CameraWrapper spyWrapper = spy(new CameraWrapper());
		doReturn(null).when(spyWrapper).openCameraFromSystem();

		try {
			spyWrapper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
		}
	}

	public void test_openCameraInUse() {
		final CameraWrapper spyWrapper = spy(new CameraWrapper());
		doThrow(new RuntimeException()).when(spyWrapper).openCameraFromSystem();

		try {
			spyWrapper.openCamera();
			fail("Missing exception");
		} catch (final OpenCameraException e) {
			assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
		}
	}

	public void test_prepareCameraShouldCallUnlock() {
		final CameraWrapper spyWrapper = spy(new CameraWrapper());
		doNothing().when(spyWrapper).unlockCameraFromSystem();

		try {
			spyWrapper.prepareCameraForRecording();
			verify(spyWrapper, times(1)).unlockCameraFromSystem();
		} catch (final PrepareCameraException e) {
			fail("Should not throw exception");
		}
	}

	public void test_prepareCameraWhenRuntimeException() {
		final CameraWrapper spyWrapper = spy(new CameraWrapper());
		doThrow(new RuntimeException()).when(spyWrapper).unlockCameraFromSystem();

		try {
			spyWrapper.prepareCameraForRecording();
			fail("Missing exception");
		} catch (final PrepareCameraException e) {
			assertEquals("Unable to use camera for recording", e.getMessage());
		}
	}

	public void test_releaseCameraWhenCameraNull() {
		final CameraWrapper wrapper = new CameraWrapper();
		wrapper.releaseCamera();
	}

	public void test_releaseCameraWhenCameraNotNull() {
		final CameraWrapper wrapper = spy(new CameraWrapper());
		doNothing().when(wrapper).releaseCameraFromSystem();
		doReturn(mock(Camera.class)).when(wrapper).getCamera();
		wrapper.releaseCamera();

		verify(wrapper, times(1)).releaseCameraFromSystem();
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
