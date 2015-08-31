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
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build.VERSION_CODES;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        doNothing().when(spyWrapper).storeCameraParametersBeforeUnlocking();

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

    public void test_getSupportedRecordingSizeTooBig() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        ArrayList<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(1920, 1080);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    public void test_getSupportedRecordingSizeTooSmall() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        ArrayList<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(320, 240);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    public void test_getSupportedVideoSizesHoneyComb() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        configureMockCameraParameters(wrapper, 640, 480, 1280, 960);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(640, supportedVideoSizes.get(0).width);
        assertEquals(480, supportedVideoSizes.get(0).height);
    }

    public void test_getSupportedVideoSizesGingerbread() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        configureMockCameraParameters(wrapper, 640, 480, 1280, 960);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.GINGERBREAD);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void configureMockCameraParameters(CameraWrapper wrapper, int videoWidth, int videoHeight, int previewWidth, int previewHeight) {
        Camera.Parameters mockParameters = mock(Camera.Parameters.class);

        ArrayList<Size> videoSizes = new ArrayList<>();
        videoSizes.add(mock(Camera.class).new Size(videoWidth, videoHeight));
        doReturn(videoSizes).when(mockParameters).getSupportedVideoSizes();

        ArrayList<Size> previewSizes = new ArrayList<>();
        previewSizes.add(mock(Camera.class).new Size(previewWidth, previewHeight));
        doReturn(previewSizes).when(mockParameters).getSupportedPreviewSizes();

        doReturn(mockParameters).when(wrapper).getCameraParametersFromSystem();
        wrapper.storeCameraParametersBeforeUnlocking();
    }
}