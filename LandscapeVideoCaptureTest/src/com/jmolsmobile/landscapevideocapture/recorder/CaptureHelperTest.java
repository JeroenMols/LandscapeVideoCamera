package com.jmolsmobile.landscapevideocapture.recorder;

import org.mockito.Mockito;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.view.Surface;

import com.jmolsmobile.landscapevideocapture.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.recorder.CaptureHelper;
import com.jmolsmobile.landscapevideocapture.recorder.OpenCameraException;
import com.jmolsmobile.landscapevideocapture.recorder.PrepareCameraException;
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

	public void test_mediaRecorderShouldHaveMediaRecorderOptions() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final MediaRecorder mockRecorder = Mockito.mock(MediaRecorder.class);
		Mockito.doReturn(mockRecorder).when(spyHelper).createMediaRecorder();

		final CaptureConfiguration config = new CaptureConfiguration();
		spyHelper.createMediaRecorder(null, null, config, null, null);

		Mockito.verify(mockRecorder, Mockito.times(1)).setOutputFormat(config.getOutputFormat());
		Mockito.verify(mockRecorder, Mockito.times(1)).setAudioSource(config.getAudioSource());
		Mockito.verify(mockRecorder, Mockito.times(1)).setVideoSource(config.getVideoSource());
		Mockito.verify(mockRecorder, Mockito.times(1)).setAudioEncoder(config.getAudioEncoder());
		Mockito.verify(mockRecorder, Mockito.times(1)).setVideoEncoder(config.getVideoEncoder());
	}

	public void test_mediaRecorderShouldHaveConfigurationOptions() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final MediaRecorder mockRecorder = Mockito.mock(MediaRecorder.class);
		Mockito.doReturn(mockRecorder).when(spyHelper).createMediaRecorder();

		final CaptureConfiguration config = new CaptureConfiguration();
		spyHelper.createMediaRecorder(null, null, config, null, null);

		Mockito.verify(mockRecorder, Mockito.times(1)).setVideoSize(config.getVideoWidth(), config.getVideoHeight());
		Mockito.verify(mockRecorder, Mockito.times(1)).setVideoEncodingBitRate(config.getVideoBitrate());
		Mockito.verify(mockRecorder, Mockito.times(1)).setMaxDuration(config.getMaxCaptureDuration());
		Mockito.verify(mockRecorder, Mockito.times(1)).setMaxFileSize(config.getMaxCaptureFileSize());
	}

	public void test_mediaRecorderShouldHaveOthernOptions() {
		final CaptureHelper spyHelper = Mockito.spy(new CaptureHelper());
		final MediaRecorder mockRecorder = Mockito.mock(MediaRecorder.class);
		Mockito.doReturn(mockRecorder).when(spyHelper).createMediaRecorder();

		final Camera mockCamera = Mockito.mock(Camera.class);
		final Surface mockSurface = Mockito.mock(Surface.class);
		final String filename = "outputfilename";
		final OnInfoListener mockListener = Mockito.mock(OnInfoListener.class);
		spyHelper.createMediaRecorder(mockCamera, mockSurface, new CaptureConfiguration(), filename, mockListener);

		Mockito.verify(mockRecorder, Mockito.times(1)).setCamera(mockCamera);
		Mockito.verify(mockRecorder, Mockito.times(1)).setPreviewDisplay(mockSurface);
		Mockito.verify(mockRecorder, Mockito.times(1)).setOutputFile(filename);
		Mockito.verify(mockRecorder, Mockito.times(1)).setOnInfoListener(mockListener);
	}
}
