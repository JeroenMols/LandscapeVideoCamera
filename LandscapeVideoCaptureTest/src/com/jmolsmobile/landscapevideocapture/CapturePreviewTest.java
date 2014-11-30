package com.jmolsmobile.landscapevideocapture;

import org.mockito.Mockito;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class CapturePreviewTest extends MockitoTestCase {

	public void test_shouldInitializeSurfaceHolder() throws Exception {
		final SurfaceHolder mockHolder = Mockito.mock(SurfaceHolder.class);
		final CapturePreview preview = new CapturePreview(null, null, mockHolder, 0, 0);

		Mockito.verify(mockHolder, Mockito.times(1)).addCallback(preview);
		Mockito.verify(mockHolder, Mockito.times(1)).setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void test_shouldNotStopPreviewWhenPreviewNotRunning() throws Exception {
		final CapturePreview spyPreview = Mockito.spy(new CapturePreview(null, Mockito.mock(Camera.class), Mockito
				.mock(SurfaceHolder.class), 0, 0));

		spyPreview.releasePreviewResources();

		Mockito.verify(spyPreview, Mockito.never()).stopPreview();
	}

	public void test_shouldStopPreviewWhenPreviewRunning() throws Exception {
		final CapturePreview spyPreview = Mockito.spy(new CapturePreview(null, Mockito.mock(Camera.class), Mockito
				.mock(SurfaceHolder.class), 0, 0));
		spyPreview.setPreviewRunning(true);

		spyPreview.releasePreviewResources();

		Mockito.verify(spyPreview, Mockito.times(1)).stopPreview();
	}
}
