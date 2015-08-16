/**
 * Copyright 2014 Jeroen Mols
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture.preview;

import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CapturePreviewTest extends MockitoTestCase {

	private final CameraWrapper	mCameraWrapper	= null;

	@SuppressWarnings("deprecation")
	public void test_shouldInitializeSurfaceHolder() throws Exception {
		final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
		final CapturePreview preview = new CapturePreview(null, mCameraWrapper, mockHolder);

		verify(mockHolder, times(1)).addCallback(preview);
		verify(mockHolder, times(1)).setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void test_shouldNotStopPreviewWhenPreviewNotRunning() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));

		preview.releasePreviewResources();

		verify(mockWrapper, never()).stopPreview();
	}

	public void test_shouldStopPreviewWhenPreviewRunning() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
		preview.setPreviewRunning(true);

		preview.releasePreviewResources();

		verify(mockWrapper, times(1)).stopPreview();
	}

	public void test_shouldNotThrowExceptionWhenStopPreviewFails() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new RuntimeException()).when(mockWrapper).stopPreview();
		final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
		preview.setPreviewRunning(true);

		try {
			preview.releasePreviewResources();
		} catch (final Exception e) {
			fail("should not throw exception");
		}
	}

	public void test_shouldStopPreviewOnStartPreviewAndPreviewRunning() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new RuntimeException()).when(mockWrapper).stopPreview();
		final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
		preview.setPreviewRunning(true);

		preview.surfaceChanged(null, 0, 0, 0);

		verify(mockWrapper, times(1)).configureForPreview(anyInt(), anyInt());
	}

	public void test_shouldCallInterfaceWhenSettingParametersFails() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new RuntimeException()).when(mockWrapper).configureForPreview(anyInt(), anyInt());
		createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldNotCallInterfaceWhenSettingAutofocusFails() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new RuntimeException()).when(mockWrapper).enableAutoFocus();
		createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

		verify(mockInterface, never()).onCapturePreviewFailed();
	}

	public void test_shouldCallInterfaceWhenStartPreviewFails1() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new IOException()).when(mockWrapper).startPreview(any(SurfaceHolder.class));
		createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldCallInterfaceWhenStartPreviewFails2() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new RuntimeException()).when(mockWrapper).startPreview(any(SurfaceHolder.class));
		createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

		verify(mockInterface, times(1)).onCapturePreviewFailed();
	}

	public void test_shouldNotCallInterfaceWhenNoExceptions() throws Exception {
		final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

		verify(mockInterface, never()).onCapturePreviewFailed();
	}

	private void createCapturePreviewAndCallSurfaceChanged(final CapturePreviewInterface mockInterface,
			final CameraWrapper mockWrapper) {
		final CapturePreview preview = new CapturePreview(mockInterface, mockWrapper, mock(SurfaceHolder.class));
		preview.surfaceChanged(null, 0, 0, 0);
	}
}
