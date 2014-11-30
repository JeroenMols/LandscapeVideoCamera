package com.jmolsmobile.landscapevideocapture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

public class CapturePreviewTest extends MockitoTestCase {

	public void test_shouldInitializeSurfaceHolder() throws Exception {
		final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
		final CapturePreview preview = new CapturePreview(null, null, mockHolder, 0, 0);

		verify(mockHolder, times(1)).addCallback(preview);
		verify(mockHolder, times(1)).setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void test_shouldNotStopPreviewWhenPreviewNotRunning() throws Exception {
		final CapturePreview spyPreview = spy(new CapturePreview(null, mock(Camera.class), mock(SurfaceHolder.class),
				0, 0));

		spyPreview.releasePreviewResources();

		verify(spyPreview, never()).stopPreview();
	}

	public void test_shouldStopPreviewWhenPreviewRunning() throws Exception {
		final CapturePreview spyPreview = spy(new CapturePreview(null, mock(Camera.class), mock(SurfaceHolder.class),
				0, 0));
		spyPreview.setPreviewRunning(true);

		spyPreview.releasePreviewResources();

		verify(spyPreview, times(1)).stopPreview();
	}

	public void test_shouldCallInterfaceWhenSettingParametersFails() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CapturePreview spyPreview = createPreviewSpyWithMockCamera(mockInterface);
		doThrow(new RuntimeException()).when(spyPreview).setCameraParameters(any(Parameters.class));

		spyPreview.surfaceChanged(null, 0, 0, 0);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldCallInterfaceWhenStartPreviewFails1() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CapturePreview spyPreview = createPreviewSpyWithMockCamera(mockInterface);
		doNothing().when(spyPreview).setCameraParameters(any(Parameters.class));
		doThrow(new IOException()).when(spyPreview).startPreview(any(SurfaceHolder.class));

		spyPreview.surfaceChanged(null, 0, 0, 0);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldCallInterfaceWhenStartPreviewFails2() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CapturePreview spyPreview = createPreviewSpyWithMockCamera(mockInterface);
		doNothing().when(spyPreview).setCameraParameters(any(Parameters.class));
		doThrow(new RuntimeException()).when(spyPreview).startPreview(any(SurfaceHolder.class));

		spyPreview.surfaceChanged(null, 0, 0, 0);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldNotCallInterfaceWhenNoExceptions() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CapturePreview spyPreview = createPreviewSpyWithMockCamera(mockInterface);
		doNothing().when(spyPreview).setCameraParameters(any(Parameters.class));
		doNothing().when(spyPreview).startPreview(any(SurfaceHolder.class));

		spyPreview.surfaceChanged(null, 0, 0, 0);

		verify(mockInterface, never()).onCapturePreviewFailed();
	}

	private CapturePreview createPreviewSpyWithMockCamera(final CapturePreviewInterface mockInterface) {
		final Camera mockCamera = mock(Camera.class);
		when(mockCamera.getParameters()).thenReturn(mock(Parameters.class));
		final CapturePreview spyPreview = spy(new CapturePreview(mockInterface, mockCamera, mock(SurfaceHolder.class),
				0, 0));
		return spyPreview;
	}
}
