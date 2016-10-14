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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
            wrapper.openCamera(false);
            verify(mockCamera, times(1)).openNativeCamera(false);
        } catch (final OpenCameraException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void openCameraNoCamera() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.openCamera(false);
            fail("Missing exception");
        } catch (final OpenCameraException e) {
            assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
        }
    }

    @Test
    public void openCameraInUse() {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doThrow(new RuntimeException()).when(mockCamera).openNativeCamera(false);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        try {
            wrapper.openCamera(false);
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
        NativeCamera mockCamera = createCameraWithMockParameters(640, 480, 0, 0);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(1920, 1080);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedRecordingSizeTooSmall() {
        NativeCamera mockCamera = createCameraWithMockParameters(640, 480, 0, 0);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(320, 240);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedVideoSizesHoneyComb() {
        NativeCamera mockCamera = createCameraWithMockParameters(640, 480, 1280, 960);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(640, supportedVideoSizes.get(0).width);
        assertEquals(480, supportedVideoSizes.get(0).height);
    }

    @Test
    public void getSupportedVideoSizesGingerbread() {
        NativeCamera mockCamera = createCameraWithMockParameters(640, 480, 1280, 960);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.GINGERBREAD);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @Test
    public void returnPreviewSizeWhenVideoSizeIsNull() {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 1280, 960);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @Test
    public void setCorrectFocusMode() throws Exception {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 0, 0);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.enableAutoFocus();

        verify(mockCamera.getNativeCameraParameters(), times(1)).setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    @Test
    public void applyFocusModeToCamera() throws Exception {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 0, 0);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.enableAutoFocus();

        verify(mockCamera, times(1)).updateNativeCameraParameters(mockCamera.getNativeCameraParameters());
    }

    @Test
    public void setPreviewFormatWhenConfiguringCamera() throws Exception {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 800, 600);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera.getNativeCameraParameters(), times(1)).setPreviewFormat(ImageFormat.NV21);
    }

    @Test
    public void setPreviewSize() throws Exception {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 300, 700);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera.getNativeCameraParameters(), times(1)).setPreviewSize(300, 700);
    }

    @Test
    public void updateParametersWhenConfiguringCamera() throws Exception {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 800, 600);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera, times(1)).updateNativeCameraParameters(mockCamera.getNativeCameraParameters());
    }

    @Test
    public void getRotationCorrectionDisplay0() throws Exception {
        CameraWrapper cameraWrapper = new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_0);

        assertEquals(0, cameraWrapper.getRotationCorrection());
    }

    @Test
    public void getRotationCorrectionDisplay90() throws Exception {
        CameraWrapper cameraWrapper = new CameraWrapper(mock(NativeCamera.class), Surface.ROTATION_90);

        assertEquals(270, cameraWrapper.getRotationCorrection());
    }

    @Test
    public void getRotationCorrectionCamera90() throws Exception {
        NativeCamera mockCamera = mock(NativeCamera.class);
        doReturn(90).when(mockCamera).getCameraOrientation();
        CameraWrapper cameraWrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        assertEquals(90, cameraWrapper.getRotationCorrection());
    }

    @Test
    public void setDisplayOrientation0WhenConfiguringCamera() {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 800, 600);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_0);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera, times(1)).setDisplayOrientation(0);
    }

    @Test
    public void setDisplayOrientation180WhenConfiguringCamera() {
        NativeCamera mockCamera = createCameraWithMockParameters(0, 0, 800, 600);
        final CameraWrapper wrapper = new CameraWrapper(mockCamera, Surface.ROTATION_180);

        wrapper.configureForPreview(800, 600);

        verify(mockCamera, times(1)).setDisplayOrientation(180);
    }

    private NativeCamera createCameraWithMockParameters(int videoWidth, int videoHeight, int previewWidth, int previewHeight) {
        NativeCamera mockCamera = mock(NativeCamera.class);
        Parameters mockParams = createMockParameters(videoWidth, videoHeight, previewWidth, previewHeight);
        doReturn(mockParams).when(mockCamera).getNativeCameraParameters();
        return mockCamera;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private Camera.Parameters createMockParameters(int videoWidth, int videoHeight, int previewWidth, int previewHeight) {
        Parameters mockParams = mock(Parameters.class);
        doReturn(createMockSize(videoWidth, videoHeight)).when(mockParams).getSupportedVideoSizes();
        doReturn(createMockSize(previewWidth, previewHeight)).when(mockParams).getSupportedPreviewSizes();
        return mockParams;
    }

    private List<Camera.Size> createMockSize(int width, int height) {
        if (width <= 0 && height <= 0) {
            return null;
        }

        Size mockSize = mock(Size.class);
        mockSize.width = width;
        mockSize.height = height;

        List<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mockSize);

        return sizes;
    }
}