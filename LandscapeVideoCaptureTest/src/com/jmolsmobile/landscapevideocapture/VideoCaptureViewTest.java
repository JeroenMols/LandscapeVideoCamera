package com.jmolsmobile.landscapevideocapture;

import android.view.View;

public class VideoCaptureViewTest extends MockitoTestCase {

	public void test_initializeClass() {
		new VideoCaptureView(getInstrumentation().getTargetContext());
	}

	public void test_viewShoudlBeInflated() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		final View container = videoCaptureView.findViewById(R.id.videocapture_container_rl);
		assertNotNull(container);
	}

	public void test_shouldNotCrashWhenListenerIsNull() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		final View recordBtn = videoCaptureView.findViewById(R.id.videocapture_recordbtn_iv);
		recordBtn.performClick();
	}
}
