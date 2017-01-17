/*
 *  Copyright 2016 Jeroen Mols
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture.preview;

import android.support.test.runner.AndroidJUnit4;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(AndroidJUnit4.class)
public class CapturePreviewTest extends MockitoTestCase {

    private final CameraWrapper mCameraWrapper = null;

    @SuppressWarnings("deprecation")
    @Test
    public void shouldInitializeSurfaceHolder() throws Exception {
        final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
        final CapturePreview preview = new CapturePreview(null, mCameraWrapper, mockHolder);

        verify(mockHolder, times(1)).addCallback(preview);
        verify(mockHolder, times(1)).setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Test
    public void shouldNotStopPreviewWhenPreviewNotRunning() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));

        preview.releasePreviewResources();

        verify(mockWrapper, never()).stopPreview();
    }

    @Test
    public void shouldStopPreviewWhenPreviewRunning() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
        preview.setPreviewRunning(true);

        preview.releasePreviewResources();

        verify(mockWrapper, times(1)).stopPreview();
    }

    @Test
    public void shouldNotThrowExceptionWhenStopPreviewFails() throws Exception {
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

    @Test
    public void shouldStopPreviewOnStartPreviewAndPreviewRunning() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
        preview.setPreviewRunning(true);

        preview.surfaceChanged(null, 0, 0, 0);

        verify(mockWrapper, times(1)).stopPreview();
    }

    @Test
    public void shouldConfigureNewPreviewOnStartPreviewAndPreviewRunning() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new RuntimeException()).when(mockWrapper).stopPreview();
        final CapturePreview preview = new CapturePreview(null, mockWrapper, mock(SurfaceHolder.class));
        preview.setPreviewRunning(true);

        preview.surfaceChanged(null, 0, 0, 0);

        verify(mockWrapper, times(1)).configureForPreview(anyInt(), anyInt());
    }

    @Test
    public void shouldCallInterfaceWhenSettingParametersFails() throws Exception {
        final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new RuntimeException()).when(mockWrapper).configureForPreview(anyInt(), anyInt());
        createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

        verify(mockInterface, times(1)).onCapturePreviewFailed();
    }

    @Test
    public void shouldNotCallInterfaceWhenSettingAutofocusFails() throws Exception {
        final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new RuntimeException()).when(mockWrapper).enableAutoFocus();
        createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

        verify(mockInterface, never()).onCapturePreviewFailed();
    }

    @Test
    public void shouldCallInterfaceWhenStartPreviewFails1() throws Exception {
        final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new IOException()).when(mockWrapper).startPreview(any(SurfaceHolder.class));
        createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

        verify(mockInterface, times(1)).onCapturePreviewFailed();
    }

    @Test
    public void shouldCallInterfaceWhenStartPreviewFails2() throws Exception {
        final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new RuntimeException()).when(mockWrapper).startPreview(any(SurfaceHolder.class));
        createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

        verify(mockInterface, times(1)).onCapturePreviewFailed();
    }

    @Test
    public void shouldNotCallInterfaceWhenNoExceptions() throws Exception {
        final CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        createCapturePreviewAndCallSurfaceChanged(mockInterface, mockWrapper);

        verify(mockInterface, never()).onCapturePreviewFailed();
    }

    @Test
    public void doNothingOnSurfaceCreated() throws Exception {
        CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        CameraWrapper mockWrapper = mock(CameraWrapper.class);
        SurfaceHolder mockHolder = mock(SurfaceHolder.class);
        CapturePreview preview = new CapturePreview(mockInterface, mockWrapper, mock(SurfaceHolder.class));

        preview.surfaceCreated(mockHolder);

        verifyNoMoreInteractions(mockInterface);
        verifyNoMoreInteractions(mockWrapper);
        verifyNoMoreInteractions(mockHolder);
    }

    @Test
    public void doNotingOnSurfaceDestroyed() throws Exception {
        CapturePreviewInterface mockInterface = mock(CapturePreviewInterface.class);
        CameraWrapper mockWrapper = mock(CameraWrapper.class);
        SurfaceHolder mockHolder = mock(SurfaceHolder.class);
        CapturePreview preview = new CapturePreview(mockInterface, mockWrapper, mock(SurfaceHolder.class));

        preview.surfaceDestroyed(mockHolder);

        verifyNoMoreInteractions(mockInterface);
        verifyNoMoreInteractions(mockWrapper);
        verifyNoMoreInteractions(mockHolder);
    }

    private void createCapturePreviewAndCallSurfaceChanged(final CapturePreviewInterface mockInterface,
                                                           final CameraWrapper mockWrapper) {
        final CapturePreview preview = new CapturePreview(mockInterface, mockWrapper, mock(SurfaceHolder.class));
        preview.surfaceChanged(mock(SurfaceHolder.class), 0, 0, 0);
    }
}
