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
import android.support.test.runner.AndroidJUnit4;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
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
    public void openCameraSuccess() {
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

    @Test
    public void openCameraNoCamera() {
        final CameraWrapper spyWrapper = spy(new CameraWrapper());
        doReturn(null).when(spyWrapper).openCameraFromSystem();

        try {
            spyWrapper.openCamera();
            fail("Missing exception");
        } catch (final OpenCameraException e) {
            assertEquals(OpenType.NOCAMERA.getMessage(), e.getMessage());
        }
    }

    @Test
    public void openCameraInUse() {
        final CameraWrapper spyWrapper = spy(new CameraWrapper());
        doThrow(new RuntimeException()).when(spyWrapper).openCameraFromSystem();

        try {
            spyWrapper.openCamera();
            fail("Missing exception");
        } catch (final OpenCameraException e) {
            assertEquals(OpenType.INUSE.getMessage(), e.getMessage());
        }
    }

    @Test
    public void prepareCameraShouldCallUnlock() {
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

    @Test
    public void prepareCameraWhenRuntimeException() {
        final CameraWrapper spyWrapper = spy(new CameraWrapper());
        doThrow(new RuntimeException()).when(spyWrapper).unlockCameraFromSystem();

        try {
            spyWrapper.prepareCameraForRecording();
            fail("Missing exception");
        } catch (final PrepareCameraException e) {
            assertEquals("Unable to use camera for recording", e.getMessage());
        }
    }

    @Test
    public void releaseCameraWhenCameraNull() {
        final CameraWrapper wrapper = new CameraWrapper();
        wrapper.releaseCamera();
    }

    @Test
    public void releaseCameraWhenCameraNotNull() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        doNothing().when(wrapper).releaseCameraFromSystem();
        doReturn(mock(Camera.class)).when(wrapper).getCamera();
        wrapper.releaseCamera();

        verify(wrapper, times(1)).releaseCameraFromSystem();
    }

    @Test
    public void prepareCameraWhenCameraNull() {
        final CameraWrapper wrapper = new CameraWrapper();

        try {
            wrapper.prepareCameraForRecording();
            fail("Missing exception");
        } catch (final PrepareCameraException e) {
            assertEquals("Unable to use camera for recording", e.getMessage());
        }
    }

    @Test
    public void getSupportedRecordingSizeTooBig() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        ArrayList<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(1920, 1080);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedRecordingSizeTooSmall() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        ArrayList<Camera.Size> sizes = new ArrayList<>();
        sizes.add(mock(Camera.class).new Size(640, 480));
        doReturn(sizes).when(wrapper).getSupportedVideoSizes(anyInt());

        RecordingSize supportedRecordingSize = wrapper.getSupportedRecordingSize(320, 240);

        assertEquals(supportedRecordingSize.width, 640);
        assertEquals(supportedRecordingSize.height, 480);
    }

    @Test
    public void getSupportedVideoSizesHoneyComb() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        configureMockCameraParameters(wrapper, 640, 480, 1280, 960);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(640, supportedVideoSizes.get(0).width);
        assertEquals(480, supportedVideoSizes.get(0).height);
    }

    @Test
    public void getSupportedVideoSizesGingerbread() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        configureMockCameraParameters(wrapper, 640, 480, 1280, 960);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.GINGERBREAD);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @Test
    public void returnPreviewSizeWhenVideoSizeIsNull() {
        final CameraWrapper wrapper = spy(new CameraWrapper());
        configureMockCameraParameters(wrapper, 0, 0, 1280, 960);

        List<Size> supportedVideoSizes = wrapper.getSupportedVideoSizes(VERSION_CODES.HONEYCOMB);

        assertEquals(1280, supportedVideoSizes.get(0).width);
        assertEquals(960, supportedVideoSizes.get(0).height);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void configureMockCameraParameters(CameraWrapper wrapper, int videoWidth, int videoHeight, int previewWidth, int previewHeight) {
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

        doReturn(mockParameters).when(wrapper).getCameraParametersFromSystem();
        wrapper.storeCameraParametersBeforeUnlocking();
    }
}