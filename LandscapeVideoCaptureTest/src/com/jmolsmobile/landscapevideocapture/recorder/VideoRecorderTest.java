package com.jmolsmobile.landscapevideocapture.recorder;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.VideoFile;

public class VideoRecorderTest extends MockitoTestCase {

	public void test_mediaRecorderShouldHaveMediaRecorderOptions() {
		final CaptureConfiguration config = new CaptureConfiguration();
		final VideoRecorder recorder = new VideoRecorder(null, config, mock(VideoFile.class), mock(SurfaceHolder.class));

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
		final VideoRecorder recorder = new VideoRecorder(null, config, mock(VideoFile.class), mock(SurfaceHolder.class));

		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		recorder.configureMediaRecorder(mockRecorder, mock(Camera.class));

		verify(mockRecorder, times(1)).setVideoSize(config.getVideoWidth(), config.getVideoHeight());
		verify(mockRecorder, times(1)).setVideoEncodingBitRate(config.getVideoBitrate());
		verify(mockRecorder, times(1)).setMaxDuration(config.getMaxCaptureDuration());
		verify(mockRecorder, times(1)).setMaxFileSize(config.getMaxCaptureFileSize());
	}

	public void test_mediaRecorderShouldHaveOthernOptions() {
		final CaptureConfiguration config = new CaptureConfiguration();
		final SurfaceHolder mockHolder = mock(SurfaceHolder.class);
		final Surface mockSurface = mock(Surface.class);
		doReturn(mockSurface).when(mockHolder).getSurface();
		final VideoFile videoFile = new VideoFile("sdcard/test.avi");
		final VideoRecorder spyRecorder = spy(new VideoRecorder(null, config, videoFile, mockHolder));
		doNothing().when(spyRecorder).initializeCameraAndPreview(mockHolder);

		final MediaRecorder mockRecorder = mock(MediaRecorder.class);
		final Camera mockCamera = mock(Camera.class);
		spyRecorder.configureMediaRecorder(mockRecorder, mockCamera);

		verify(mockRecorder, times(1)).setCamera(mockCamera);
		verify(mockRecorder, times(1)).setPreviewDisplay(mockSurface);
		verify(mockRecorder, times(1)).setOutputFile(videoFile.getFullPath());
		verify(mockRecorder, times(1)).setOnInfoListener(spyRecorder);
	}
}
