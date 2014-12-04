package com.jmolsmobile.landscapevideocapture.recorder;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.VideoFile;
import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;
import com.jmolsmobile.landscapevideocapture.camera.PrepareCameraException;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;

import java.io.IOException;

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
public class VideoRecorderTest extends MockitoTestCase {

	public void test_openCameraWhenCreated() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		new VideoRecorder(null, mock(CaptureConfiguration.class), null, mockWrapper, mock(SurfaceHolder.class));
		verify(mockWrapper, times(1)).openCamera();
	}

	public void test_callListenerWhenOpenCameraFails() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new OpenCameraException(OpenType.NOCAMERA)).when(mockWrapper).openCamera();
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		new VideoRecorder(mockInterface, null, null, mockWrapper, mock(SurfaceHolder.class));
		verify(mockInterface, times(1)).onRecordingFailed(anyString());
	}

	public void test_startRecordingWhenNotStarted() throws Exception {
		final VideoRecorder spyRecorder = spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null,
				mock(CameraWrapper.class), mock(SurfaceHolder.class)));
		doNothing().when(spyRecorder).startRecording();

		spyRecorder.toggleRecording();

		verify(spyRecorder, times(1)).startRecording();
	}

	public void test_stopRecordingWhenStarted() throws Exception {
		final VideoRecorder spyRecorder = spy(new VideoRecorder(null, mock(CaptureConfiguration.class), null,
				mock(CameraWrapper.class), mock(SurfaceHolder.class)));
		doReturn(true).when(spyRecorder).isRecording();
		doNothing().when(spyRecorder).stopRecording(anyString());

		spyRecorder.toggleRecording();

		verify(spyRecorder, times(1)).stopRecording(null);
	}

	public void test_notifyFailedWhenCameraDoesNotPrepare() throws Exception {
		final CameraWrapper mockWrapper = mock(CameraWrapper.class);
		doThrow(new PrepareCameraException()).when(mockWrapper).prepareCameraForRecording();
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder recorder = new VideoRecorder(mockInterface, mock(CaptureConfiguration.class), null,
				mockWrapper, mock(SurfaceHolder.class));

		recorder.startRecording();

		verify(mockInterface, times(1)).onRecordingFailed("Unable to record video");
	}

	public void test_notifyStartedWhenRecordingStarts() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final MediaRecorder mockRecorder = mock(MediaRecorder.class);

		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
		spyRecorder.startRecording();

		verify(mockInterface, times(1)).onRecordingStarted();
	}

	private VideoRecorder createSpyRecorder(final VideoRecorderInterface mockInterface, final MediaRecorder mockRecorder) {
		final VideoRecorder spyRecorder = spy(new VideoRecorder(mockInterface, mock(CaptureConfiguration.class),
				mock(VideoFile.class), mock(CameraWrapper.class), mock(SurfaceHolder.class)));
		doReturn(mockRecorder).when(spyRecorder).getMediaRecorder();
		return spyRecorder;
	}

	public void test_dontStartRecordingWhenPreparationFails() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		doThrow(new IllegalStateException()).when(mockRecorder).prepare();

		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
		spyRecorder.startRecording();

		verify(mockRecorder, never()).start();
	}

	public void test_dontStartRecordingWhenPreparationFails2() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		doThrow(new IOException()).when(mockRecorder).prepare();

		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
		spyRecorder.startRecording();

		verify(mockRecorder, never()).start();
	}

	public void test_dontNotifyListenerWhenStartFailsInIllegalState() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		doThrow(new IllegalStateException()).when(mockRecorder).start();

		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
		spyRecorder.startRecording();

		verify(mockInterface, never()).onRecordingStarted();
	}

	public void test_notifyListenerWhenStartFails() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		doThrow(new RuntimeException()).when(mockRecorder).start();

		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, mockRecorder);
		spyRecorder.startRecording();

		verify(mockInterface, times(1)).onRecordingFailed("Unable to record video with given settings");
	}

	public void test_dontNotifyListenerWhenNotStopped() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, null, false);

		spyRecorder.stopRecording(null);

		verify(mockInterface, never()).onRecordingStopped(anyString());
	}

	public void test_notifyListenerWhenStoppedFailed() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, null, true);

		spyRecorder.stopRecording("test");

		verify(mockInterface, never()).onRecordingSuccess();
		verify(mockInterface, times(1)).onRecordingStopped("test");
	}

	public void test_notifyListenerWhenStoppedSuccess() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder spyRecorder = createSpyRecorderForStopTests(mockInterface, mock(MediaRecorder.class), true);

		spyRecorder.stopRecording("test");

		verify(mockInterface, times(1)).onRecordingSuccess();
		verify(mockInterface, times(1)).onRecordingStopped("test");
	}

	public void test_mediaRecorderShouldHaveMediaRecorderOptions() {
		final CaptureConfiguration config = new CaptureConfiguration();
		final VideoRecorder recorder = new VideoRecorder(null, config, mock(VideoFile.class), mock(CameraWrapper.class),
				mock(SurfaceHolder.class));

		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		recorder.configureMediaRecorder(mockRecorder, mock(Camera.class));

		verify(mockRecorder, times(1)).setOutputFormat(config.getOutputFormat());
		verify(mockRecorder, times(1)).setAudioSource(config.getAudioSource());
		verify(mockRecorder, times(1)).setVideoSource(config.getVideoSource());
		verify(mockRecorder, times(1)).setAudioEncoder(config.getAudioEncoder());
		verify(mockRecorder, times(1)).setVideoEncoder(config.getVideoEncoder());
	}

	public void test_mediaRecorderShouldHaveConfigurationOptions() {
		final CaptureConfiguration config = new CaptureConfiguration();
		final VideoRecorder recorder = new VideoRecorder(null, config, mock(VideoFile.class), mock(CameraWrapper.class),
				mock(SurfaceHolder.class));

		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		recorder.configureMediaRecorder(mockRecorder, mock(Camera.class));

		verify(mockRecorder, times(1)).setVideoSize(config.getVideoWidth(), config.getVideoHeight());
		verify(mockRecorder, times(1)).setVideoEncodingBitRate(config.getVideoBitrate());
		verify(mockRecorder, times(1)).setMaxDuration(config.getMaxCaptureDuration());
		verify(mockRecorder, times(1)).setMaxFileSize(config.getMaxCaptureFileSize());
	}

	public void test_mediaRecorderShouldHaveOtherOptions() {
		final CaptureConfiguration config = new CaptureConfiguration();
		final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
		final Surface mockSurface = mock(Surface.class);
		doReturn(mockSurface).when(mockHolder).getSurface();
		final VideoFile videoFile = new VideoFile("sdcard/test.avi");
		final VideoRecorder spyRecorder = spy(new VideoRecorder(null, config, videoFile, mock(CameraWrapper.class),
				mockHolder));
		doNothing().when(spyRecorder).initializeCameraAndPreview(mockHolder);

		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		final Camera mockCamera = mock(Camera.class);
		spyRecorder.configureMediaRecorder(mockRecorder, mockCamera);

		verify(mockRecorder, times(1)).setCamera(mockCamera);
		verify(mockRecorder, times(1)).setPreviewDisplay(mockSurface);
		verify(mockRecorder, times(1)).setOutputFile(videoFile.getFullPath());
		verify(mockRecorder, times(1)).setOnInfoListener(spyRecorder);
	}

	public void test_dontStopRecordingWhenUnknownInfo() throws Exception {
		final VideoRecorder spyRecorder = createSpyRecorder(null, null);
		spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN, 0);
		verify(spyRecorder, never()).stopRecording(anyString());
	}

	public void test_notifyListenerWhenMaxFileSize() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
		doReturn(true).when(spyRecorder).isRecording();

		spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED, 0);

		verify(mockInterface, times(1)).onRecordingStopped("Capture stopped - Max file size reached");
	}

	public void test_notifyListenerWhenMaxDuration() throws Exception {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
		doReturn(true).when(spyRecorder).isRecording();

		spyRecorder.onInfo(null, MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED, 0);

		verify(mockInterface, times(1)).onRecordingStopped("Capture stopped - Max duration reached");
	}

	public void test_notifyListenerWhenPreviewFails() {
		final VideoRecorderInterface mockInterface = mock(VideoRecorderInterface.class);
		final VideoRecorder recorder = new VideoRecorder(mockInterface, mock(CaptureConfiguration.class), null,
				mock(CameraWrapper.class), mock(SurfaceHolder.class));
		recorder.onCapturePreviewFailed();

		verify(mockInterface, times(1)).onRecordingFailed("Unable to show camera preview");
	}

	private VideoRecorder createSpyRecorderForStopTests(final VideoRecorderInterface mockInterface,
			final MediaRecorder recorder, final boolean isRecording) {
		final VideoRecorder spyRecorder = createSpyRecorder(mockInterface, null);
		doReturn(isRecording).when(spyRecorder).isRecording();
		doReturn(recorder).when(spyRecorder).getMediaRecorder();
		return spyRecorder;
	}
}
