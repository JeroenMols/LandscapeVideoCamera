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

package com.jmolsmobile.landscapevideocapture.recorder;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.test.runner.AndroidJUnit4;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.VideoFile;
import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;
import com.jmolsmobile.landscapevideocapture.camera.PrepareCameraException;
import com.jmolsmobile.landscapevideocapture.camera.RecordingSize;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("deprecation")
@RunWith(AndroidJUnit4.class)
public class VideoRecorderTest extends MockitoTestCase {

    @Test
    public void openCameraWhenCreated() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        new VideoRecorder(null, mock(CaptureConfiguration.class), null, mockWrapper, mock(SurfaceHolder.class));
        verify(mockWrapper, times(1)).openCamera();
    }

    @Test
    public void callListenerWhenOpenCameraFails() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new OpenCameraException(OpenType.NOCAMERA)).when(mockWrapper).openCamera();
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        new VideoRecorder(mockInterface, null, null, mockWrapper, mock(SurfaceHolder.class));
        verify(mockInterface, times(1)).onRecordingFailed(anyString());
    }

    @Test
    public void startRecordingWhenNotStarted() throws Exception {
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null, mock(CameraWrapper.class), mock(SurfaceHolder.class)));
        doNothing().when(spyRecorder).startRecording();

        spyRecorder.toggleRecording();

        verify(spyRecorder, times(1)).startRecording();
    }

    @Test
    public void stopRecordingWhenStarted() throws Exception {
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null, mock(CameraWrapper.class), mock(SurfaceHolder.class)));
        doReturn(true).when(spyRecorder).isRecording();
        doNothing().when(spyRecorder).stopRecording(anyString());

        spyRecorder.toggleRecording();

        verify(spyRecorder, times(1)).stopRecording(null);
    }

    @Test(expected = AlreadyUsedException.class)
    public void throwExceptionAfterReleaseAllResources() throws Exception {
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null, mock(CameraWrapper.class), mock(SurfaceHolder.class)));
        spyRecorder.releaseAllResources();

        spyRecorder.toggleRecording();
    }

    @Test
    public void notifyFailedWhenCameraDoesNotPrepare() throws Exception {
        final CameraWrapper mockWrapper = mock(CameraWrapper.class);
        doThrow(new PrepareCameraException()).when(mockWrapper).prepareCameraForRecording();
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder recorder = new VideoRecorder(mockInterface, mock(CaptureConfiguration.class), null, mockWrapper, mock(SurfaceHolder.class));

        recorder.startRecording();

        verify(mockInterface, times(1)).onRecordingFailed("Unable to record video");
    }

    @Test
    public void notifyStartedWhenRecordingStarts() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final MediaRecorder mockRecorder = mock(MediaRecorder.class);

        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
        spyRecorder.startRecording();

        verify(mockInterface, times(1)).onRecordingStarted();
    }

    @Test
    public void dontStartRecordingWhenPreparationFails() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new IllegalStateException()).when(mockRecorder).prepare();

        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
        spyRecorder.startRecording();

        verify(mockRecorder, never()).start();
    }

    @Test
    public void dontStartRecordingWhenPreparationFails2() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new IOException()).when(mockRecorder).prepare();

        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
        spyRecorder.startRecording();

        verify(mockRecorder, never()).start();
    }

    @Test
    public void dontNotifyListenerWhenStartFailsInIllegalState() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new IllegalStateException()).when(mockRecorder).start();

        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
        spyRecorder.startRecording();

        verify(mockInterface, never()).onRecordingStarted();
    }

    @Test
    public void notifyListenerWhenStartFails() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new RuntimeException()).when(mockRecorder).start();

        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
        spyRecorder.startRecording();

        verify(mockInterface, times(1)).onRecordingFailed("Unable to record video with given settings");
    }

    @Test
    public void dontNotifyListenerWhenNotStopped() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, null, false);

        spyRecorder.stopRecording(null);

        verify(mockInterface, never()).onRecordingStopped(anyString());
    }

    @Test
    public void notifyListenerWhenStoppedFailed() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, null, true);

        spyRecorder.stopRecording("test");

        verify(mockInterface, never()).onRecordingSuccess();
        verify(mockInterface, times(1)).onRecordingStopped("test");
    }

    @Test
    public void notifyListenerWhenStoppedSuccess() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, mock(MediaRecorder.class), true);

        spyRecorder.stopRecording("test");

        verify(mockInterface, times(1)).onRecordingSuccess();
        verify(mockInterface, times(1)).onRecordingStopped("test");
    }

    @Test
    public void mediaRecorderShouldHaveMediaRecorderOptions() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();
        CamcorderProfile profile = getEmptyCamcorderProfile();
        final VideoRecorder recorder =
                new VideoRecorder(null, config, mock(VideoFile.class), createMockCameraWrapperForPrepare(profile), mock(SurfaceHolder.class));

        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        recorder.configureMediaRecorder(mockRecorder, mock(Camera.class));

        verify(mockRecorder, times(1)).setAudioSource(config.getAudioSource());
        verify(mockRecorder, times(1)).setVideoSource(config.getVideoSource());
        assertEquals(profile.fileFormat, config.getOutputFormat());
        assertEquals(profile.audioCodec, config.getAudioEncoder());
        assertEquals(profile.videoCodec, config.getVideoEncoder());
        verify(mockRecorder, times(1)).setProfile(profile);
    }

    @Test
    public void mediaRecorderShouldHaveConfigurationOptions() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();
        CamcorderProfile profile = getEmptyCamcorderProfile();
        CameraWrapper mockWrapper = createMockCameraWrapperForPrepare(profile);
        doReturn(new RecordingSize(777, 888)).when(mockWrapper).getSupportedRecordingSize(anyInt(), anyInt());
        final VideoRecorder recorder = new VideoRecorder(null, config, mock(VideoFile.class), mockWrapper, mock(SurfaceHolder.class));

        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        recorder.configureMediaRecorder(mockRecorder, mock(Camera.class));

        assertEquals(profile.videoFrameWidth, 777);
        assertEquals(profile.videoFrameHeight, 888);
        assertEquals(profile.videoBitRate, config.getVideoBitrate());
        verify(mockRecorder, times(1)).setMaxDuration(config.getMaxCaptureDuration());
        verify(mockRecorder, times(1)).setProfile(profile);
        verify(mockRecorder, times(1)).setMaxFileSize(config.getMaxCaptureFileSize());
    }

    @Test
    public void mediaRecorderShouldHaveOtherOptions() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();
        final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
        final Surface mockSurface = mock(Surface.class);
        doReturn(mockSurface).when(mockHolder).getSurface();
        final VideoFile videoFile = new VideoFile("sdcard/test.avi");
        final VideoRecorder spyRecorder = spy(new VideoRecorder(null, config, videoFile, createMockCameraWrapperForInitialisation(), mockHolder));
        doNothing().when(spyRecorder).initializeCameraAndPreview(mockHolder);

        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        final Camera mockCamera = mock(Camera.class);
        spyRecorder.configureMediaRecorder(mockRecorder, mockCamera);

        verify(mockRecorder, times(1)).setCamera(mockCamera);
        verify(mockRecorder, never()).setPreviewDisplay(any(Surface.class));
        verify(mockRecorder, times(1)).setOutputFile(videoFile.getFullPath());
        verify(mockRecorder, times(1)).setOnInfoListener(spyRecorder);
    }

    @Test
    public void continueInitialisationWhenSetMaxFilesizeFails() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(null, config, mock(VideoFile.class), createMockCameraWrapperForInitialisation(), mock(SurfaceHolder.class)));

        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new IllegalArgumentException()).when(mockRecorder).setMaxFileSize(anyInt());

        try {
            spyRecorder.configureMediaRecorder(mockRecorder, mock(Camera.class));
            verify(mockRecorder, times(1)).setOnInfoListener(spyRecorder);
        } catch (RuntimeException e) {
            fail("Crashed when setting max filesize");
        }
    }

    @Test
    public void continueInitialisationWhenSetMaxFilesizeFails2() throws Exception {
        final CaptureConfiguration config = new CaptureConfiguration();
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(null, config, mock(VideoFile.class), createMockCameraWrapperForInitialisation(), mock(SurfaceHolder.class)));

        final MediaRecorder mockRecorder = mock(MediaRecorder.class);
        doThrow(new RuntimeException()).when(mockRecorder).setMaxFileSize(anyInt());

        try {
            spyRecorder.configureMediaRecorder(mockRecorder, mock(Camera.class));
            verify(mockRecorder, times(1)).setOnInfoListener(spyRecorder);
        } catch (RuntimeException e) {
            fail("Crashed when setting max filesize");
        }
    }

    @Test
    public void dontStopRecordingWhenUnknownInfo() throws Exception {
        final VideoRecorder spyRecorder = createSpyRecorder(null, null);
        spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN, 0);
        verify(spyRecorder, never()).stopRecording(anyString());
    }

    @Test
    public void notifyListenerWhenMaxFileSize() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
        doReturn(true).when(spyRecorder).isRecording();

        spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED, 0);

        verify(mockInterface, times(1)).onRecordingStopped("Capture stopped - Max file size reached");
    }

    @Test
    public void notifyListenerWhenMaxDuration() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
        doReturn(true).when(spyRecorder).isRecording();

        spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED, 0);

        verify(mockInterface, times(1)).onRecordingStopped("Capture stopped - Max duration reached");
    }

    @Test
    public void notifyListenerWhenPreviewFails() throws Exception {
        final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
        final VideoRecorder recorder =
                new VideoRecorder(mockInterface, mock(CaptureConfiguration.class), null, mock(CameraWrapper.class), mock(SurfaceHolder.class));
        recorder.onCapturePreviewFailed();

        verify(mockInterface, times(1)).onRecordingFailed("Unable to show camera preview");
    }

    @Test
    public void shouldNotCrashAfterDoubleRelease() {
        final VideoRecorder spyRecorder =
                Mockito.spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null, mock(CameraWrapper.class), mock(SurfaceHolder.class)));
        doNothing().when(spyRecorder).configureMediaRecorder(any(MediaRecorder.class), any(Camera.class));

        spyRecorder.releaseAllResources();
        try {
            spyRecorder.releaseAllResources();
        } catch (RuntimeException e) {
            fail("Failed to call releaseAllResources twice");
        }

    }

    private VideoRecorder createSpyRecorderForStopTests(final VideoRecorderInterface mockInterface, final MediaRecorder recorder, final boolean isRecording) {
        final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
        doReturn(isRecording).when(spyRecorder).isRecording();
        doReturn(recorder).when(spyRecorder).getMediaRecorder();
        return spyRecorder;
    }

    private VideoRecorder createSpyRecorder(final VideoRecorderInterface mockInterface, final MediaRecorder mockRecorder) {
        final VideoRecorder spyRecorder =
                spy(new VideoRecorder(mockInterface, mock(CaptureConfiguration.class), mock(VideoFile.class), createMockCameraWrapperForInitialisation(),
                        mock(SurfaceHolder.class)));
        doReturn(mockRecorder).when(spyRecorder).getMediaRecorder();
        return spyRecorder;
    }

    private CameraWrapper createMockCameraWrapperForInitialisation() {
        return createMockCameraWrapperForPrepare(getEmptyCamcorderProfile());
    }

    private CameraWrapper createMockCameraWrapperForPrepare(CamcorderProfile profile) {
        CameraWrapper mock = mock(CameraWrapper.class);
        doReturn(new RecordingSize(320, 240)).when(mock).getSupportedRecordingSize(anyInt(), anyInt());
        doReturn(profile).when(mock).getBaseRecordingProfile();
        return mock;
    }

    private CamcorderProfile getEmptyCamcorderProfile() {
        try {
            Constructor<CamcorderProfile> constructor =
                    CamcorderProfile.class.getDeclaredConstructor(new Class[]{int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                                                                              int.class, int.class, int.class, int.class, int.class});
            constructor.setAccessible(true);
            return constructor.newInstance(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        } catch (Exception e) {
        }
        return null;
    }
}
