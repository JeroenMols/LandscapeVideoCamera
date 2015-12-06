/**
 * Copyright 2014 Jeroen Mols
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture.camera;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build.VERSION_CODES;
import android.support.test.runner.AndroidJUnit4;
import android.view.Surface;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("deprecation")
@RunWith(AndroidJUnit4.class)
public class CameraWrapperTest extends MockitoTestCase {

    @Test
    public void canInitialize() {
        new CameraWrapper(new NativeCamera(), Surface.ROTATION_90);
    }

    @Test
    public void openCameraSuccess() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doReturn(mock(Camera.class)).when(mockCamera).getNativeCamera();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.openCamera();
            verify(mockCamera, times(1)).openNativeCamera();
        } catch (final OpenCameraException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void openCameraNoCamera() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.openCamera();
            fail("Missing exception");
        } catch (final OpenCameraException e) {
            assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
        }
    }

    @Test
    public void openCameraInUse() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doThrow(new RuntimeException()).when(mockCamera).openNativeCamera();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.openCamera();
            fail("Missing exception");
        } catch (final OpenCameraException e) {
            assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
        }
    }

    @Test
    public void prepareCameraShouldCallUnlock() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.prepareCameraForRecording();
            verify(mockCamera, times(1)).unlockNativeCamera();
        } catch (final PrepareCameraException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void prepareCameraWhenRuntimeException() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doThrow(new RuntimeException()).when(mockCamera).unlockNativeCamera();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.prepareCameraForRecording();
            fail("Missing exception");
        } catch (final PrepareCameraException e) {
            assertEquals("Unable to use camera for recording", e.getMessage());
        }
    }

    @Test
    public void releaseCameraWhenCameraNull() {
        final CameraWrapper wrapper = new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_0);
        wrapper.releaseCamera();
    }

    @Test
    public void releaseCameraWhenCameraNotNull() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doReturn(mock(Camera.class)).when(mockCamera).getNativeCamera();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);
        wrapper.releaseCamera();

        verify(mockCamera, times(1)).releaseNativeCamera();
    }

    @Test
    public void prepareCameraWhenCameraNull() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doThrow(new NullPointerException()).when(mockCamera).unlockNativeCamera();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.prepareCameraForRecording();
            fail("Missing exception");
        } catch (final PrepareCameraException e) {
            assertEquals("Unable to use camera for recording", e.getMessage());
        }
    }

    @Test
    public void prepareCameraWhenCameraNotNull() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.prepareCameraForRecording();
            verify(mockCamera, times(1)).unlockNativeCamera();
        } catch (final PrepareCameraException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void getSupportedRecordingSizeTooBig() {
        final CameraWrapper wrapper = spy(new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_0));
        ArrayList<Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(1920, 1080);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedRecordingSizeTooSmall() {
        final CameraWrapper wrapper = spy(new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_0));
        ArrayList<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(320, 240);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedVideoSizesHoneyComb() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        configureMockCameraParameters(mockCamera, 640, 480, 1280, 960);
        final CameraWrapper wrapper = spy(new CameraWrapper(mockCamera, Surface.ROTATION_0));

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(640, supportedVideoSizes.get(0).width);
        assertEquals(480, supportedVideoSizes.get(0).height);
    }

    @Test
    public void getSupportedVideoSizesGingerbread() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        configureMockCameraParameters(mockCamera, 640, 480, 1280, 960);
        final CameraWrapper wrapper = spy(new CameraWrapper(mockCamera, Surface.ROTATION_0));

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.GINGERBREAD);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @Test
    public void returnPreviewSizeWhenVideoSizeIsNull() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        configureMockCameraParameters(mockCamera, 0, 0, 1280, 960);
        final CameraWrapper wrapper = spy(new CameraWrapper(mockCamera, Surface.ROTATION_0));

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @Test
    public void setCorrectFocusMode() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParameters = mock(Parameters.class);
        doReturn(mockParameters).when(mockCamera).getNativeCameraParameters();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.enableAutoFocus();

        verify(mockParameters, times(1)).setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    @Test
    public void applyFocusModeToCamera() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParameters = mock(Parameters.class);
        doReturn(mockParameters).when(mockCamera).getNativeCameraParameters();
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.enableAutoFocus();

        verify(mockCamera, times(1)).updateNativeCameraParameters(mockParameters);
    }

    @Test
    public void setPreviewFormatWhenConfiguringCamera() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParameters = mock(Parameters.class);
        doReturn(mockParameters).when(mockCamera).getNativeCameraParameters();
        final CameraWrapper wrapper = createSpyWrapperWithOptimalSize(mockCamera, 0, 0);

        wrapper.configureForPreview(800, 600);

        verify(mockParameters, times(1)).setPreviewFormat(ImageFormat.NV21);
    }

    @Test
    public void setPreviewSizeWhenConfiguringCamera() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParameters = mock(Parameters.class);
        doReturn(mockParameters).when(mockCamera).getNativeCameraParameters();
        final CameraWrapper wrapper = createSpyWrapperWithOptimalSize(mockCamera, 300, 700);

        wrapper.configureForPreview(800, 600);

        verify(mockParameters, times(1)).setPreviewSize(300, 700);
    }

    @Test
    public void updateParametersWhenConfiguringCamera() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParameters = mock(Parameters.class);
        doReturn(mockParameters).when(mockCamera).getNativeCameraParameters();
        final CameraWrapper wrapper = createSpyWrapperWithOptimalSize(mockCamera, 0, 0);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera, times(1)).updateNativeCameraParameters(mockParameters);
    }

    @Test
    public void getDisplayOrientation0() throws Exception {
        CameraWrapper cameraWrapper = new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_0);

        assertEquals(0, cameraWrapper.getDisplayOrientation());
    }

    @Test
    public void getDisplayOrientation90() throws Exception {
        CameraWrapper cameraWrapper = new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_90);

        assertEquals(90, cameraWrapper.getDisplayOrientation());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private Camera.Parameters configureMockCameraParameters(NativeCamera camera, int videoWidth, int videoHeight, int previewWidth, int previewHeight) {
        Camera.Parameters mockParameters = mock(Camera.Parameters.class);

        if (videoWidth > 0 && videoHeight > 0) {
            ArrayList<Size> videoSizes = new ArrayList<>();
            videoSizes.add(mock(Camera.class).new Size(videoWidth, videoHeight));
            doReturn(videoSizes).when(mockParameters).getSupportedVideoSizes();
        } else {
            doReturn(null).when(mockParameters).getSupportedVideoSizes();
        }

        ArrayList<Size> previewSizes = new ArrayList<>();
        previewSizes.add(mock(Camera.class).new Size(previewWidth, previewHeight));
        doReturn(previewSizes).when(mockParameters).getSupportedPreviewSizes();

        doReturn(mockParameters).when(camera).getNativeCameraParameters();

        return mockParameters;
    }

    private CameraWrapper createSpyWrapperWithOptimalSize(NativeCamera mockCamera, int optimalWidth, int optimalHeight) {
        final CameraWrapper wrapper = spy(new CameraWrapper(mockCamera, Surface.ROTATION_0));
        CameraSize optimalSize = new CameraSize(optimalWidth, optimalHeight);
        doReturn(optimalSize).when(wrapper).getOptimalSize(any(List.class), anyInt(), anyInt());
        return wrapper;
    }
}