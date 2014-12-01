package com.jmolsmobile.landscapevideocapture.view;

import org.mockito.Mockito;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.R;
import com.jmolsmobile.landscapevideocapture.R.id;
import com.jmolsmobile.landscapevideocapture.view.RecordingButtonInterface;
import com.jmolsmobile.landscapevideocapture.view.VideoCaptureView;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Jeroen Mols
 */
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

	public void test_uiAfterUpdateFinishedBitmapNull() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		videoCaptureView.updateUIRecordingFinished(null);
		checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.INVISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.GONE);
	}

	public void test_uiAfterUpdateFinishedBitmapNotNull() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		final ImageView imageView = (ImageView) videoCaptureView.findViewById(R.id.videocapture_preview_iv);
		final Drawable background = imageView.getDrawable();
		videoCaptureView.updateUIRecordingFinished(Bitmap.createBitmap(10, 10, Config.ARGB_4444));
		checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.INVISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.VISIBLE);
		checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.GONE);
		assertNotSame(background, imageView.getDrawable());
	}

	public void test_surfaceViewShouldNotBeNull() {
		final VideoCaptureView videoCaptureView = new VideoCaptureView(getInstrumentation().getTargetContext());
		assertNotNull(videoCaptureView.getPreviewSurfaceHolder());
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
