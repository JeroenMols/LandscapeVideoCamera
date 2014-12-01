package com.jmolsmobile.landscapevideocapture;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.Surface;

import com.jmolsmobile.landscapevideocapture.OpenCameraException.OpenType;

/**
 * @author Jeroen Mols
 */
public class CaptureHelper {

	public Camera openCamera() throws OpenCameraException {
		Camera camera = null;
		try {
			camera = openCameraFromSystem();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new OpenCameraException(OpenType.INUSE);
		}

		if (camera == null) throw new OpenCameraException(OpenType.NOCAMERA);
		return camera;
	}

	public void prepareCameraForRecording(Camera camera) throws PrepareCameraException {
		try {
			unlockCameraFromSystem(camera);
		} catch (final RuntimeException e) {
			throw new PrepareCameraException();
		}
	}

	public MediaRecorder createMediaRecorder(Camera camera, Surface previewSurface,
			CaptureConfiguration captureConfiguration, String outputFilename, OnInfoListener recordingListener) {

		final MediaRecorder recorder = createMediaRecorder();
		recorder.setCamera(camera);
		recorder.setAudioSource(captureConfiguration.getAudioSource());
		recorder.setVideoSource(captureConfiguration.getVideoSource());

		// Order is important
		recorder.setOutputFormat(captureConfiguration.getOutputFormat());
		recorder.setMaxDuration(captureConfiguration.getMaxCaptureDuration());
		recorder.setOutputFile(outputFilename);

		recorder.setVideoSize(captureConfiguration.getVideoWidth(), captureConfiguration.getVideoHeight());
		recorder.setVideoEncodingBitRate(captureConfiguration.getVideoBitrate());

		recorder.setAudioEncoder(captureConfiguration.getAudioEncoder());
		recorder.setVideoEncoder(captureConfiguration.getVideoEncoder());

		recorder.setPreviewDisplay(previewSurface);
		recorder.setMaxFileSize(captureConfiguration.getMaxCaptureFileSize());
		recorder.setOnInfoListener(recordingListener);

		return recorder;
	}

	@SuppressWarnings("deprecation")
	public Bitmap generateThumbnail(String outputFilename) {
		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(outputFilename, Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail == null) {
			CLog.d(CLog.HELPER, "Failed to generate video preview");
		}
		return thumbnail;
	}

	protected Camera openCameraFromSystem() {
		return Camera.open(CameraInfo.CAMERA_FACING_BACK);
	}

	protected void unlockCameraFromSystem(Camera camera) {
		camera.unlock();
	}

	protected MediaRecorder createMediaRecorder() {
		return new MediaRecorder();
	}

}
