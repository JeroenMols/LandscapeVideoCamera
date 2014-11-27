package com.jmolsmobile.landscapevideocapture;

import org.mockito.Mockito;

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

	public void test_recordBtnShouldNotifyListener() {
		final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
		performClickOnButton(R.id.videocapture_recordbtn_iv, mockBtnInterface);
		Mockito.verify(mockBtnInterface, Mockito.times(1)).onRecordButtonClicked();
	}

	public void test_acceptBtnShouldNotifyListener() {
		final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
		performClickOnButton(R.id.videocapture_acceptbtn_iv, mockBtnInterface);
		Mockito.verify(mockBtnInterface, Mockito.times(1)).onAcceptButtonClicked();
	}

	public void test_declineBtnShouldNotifyListener() {
		final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
		performClickOnButton(R.id.videocapture_declinebtn_iv, mockBtnInterface);
		Mockito.verify(mockBtnInterface, Mockito.times(1)).onDeclineButtonClicked();
	}

	public void test_uiShouldBeNotRecordingByDefault() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		checkUINotRecording(videoCaptureView);
	}

	public void test_uiAfterUpdateNotRecording() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		videoCaptureView.updateUINotRecording();
		checkUINotRecording(videoCaptureView);
	}

	public void test_uiAfterUpdateOngoing() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		videoCaptureView.updateUIRecordingOngoing();
		assertTrue(videoCaptureView.findViewById(R.id.videocapture_recordbtn_iv).isSelected());
		checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.VISIBLE);
	}

	private void checkUINotRecording(final VideoCaptureView videoCaptureView) {
		assertFalse(videoCaptureView.findViewById(R.id.videocapture_recordbtn_iv).isSelected());
		checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.GONE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.VISIBLE);
	}

	private void checkVisibility(final VideoCaptureView videoCaptureView, int videocaptureRecordbtnIv, int visible) {
		assertTrue(videoCaptureView.findViewById(videocaptureRecordbtnIv).getVisibility() == visible);
	}

	private void performClickOnButton(int btnResourceId, final RecordingButtonInterface mockBtnInterface) {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		videoCaptureView.setRecordingButtonInterface(mockBtnInterface);
		final View btn = videoCaptureView.findViewById(btnResourceId);
		btn.performClick();
	}

}
