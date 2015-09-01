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

package com.jmolsmobile.landscapevideocapture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageView;

import com.jmolsmobile.landscapevideocapture.MockitoTestCase;
import com.jmolsmobile.landscapevideocapture.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VideoCaptureViewTest extends MockitoTestCase {

    @Rule
    public final UiThreadTestRule uiThread = new UiThreadTestRule();

    @UiThreadTest
    @Test
    public void initializeClass() {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        new VideoCaptureView(targetContext);
    }

    @UiThreadTest
    @Test
    public void viewShoudlBeInflated() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        final View container = videoCaptureView.findViewById(R.id.videocapture_container_rl);
        assertNotNull(container);
    }

    @UiThreadTest
    @Test
    public void shouldNotCrashWhenListenerIsNull() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        final View recordBtn = videoCaptureView.findViewById(R.id.videocapture_recordbtn_iv);
        recordBtn.performClick();
    }

    @UiThreadTest
    @Test
    public void recordBtnShouldNotifyListener() {
        final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
        performClickOnButton(R.id.videocapture_recordbtn_iv, mockBtnInterface);
        Mockito.verify(mockBtnInterface, Mockito.times(1)).onRecordButtonClicked();
    }

    @UiThreadTest
    @Test
    public void acceptBtnShouldNotifyListener() {
        final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
        performClickOnButton(R.id.videocapture_acceptbtn_iv, mockBtnInterface);
        Mockito.verify(mockBtnInterface, Mockito.times(1)).onAcceptButtonClicked();
    }

    @UiThreadTest
    @Test
    public void declineBtnShouldNotifyListener() {
        final RecordingButtonInterface mockBtnInterface = Mockito.mock(RecordingButtonInterface.class);
        performClickOnButton(R.id.videocapture_declinebtn_iv, mockBtnInterface);
        Mockito.verify(mockBtnInterface, Mockito.times(1)).onDeclineButtonClicked();
    }

    @UiThreadTest
    @Test
    public void uiShouldBeNotRecordingByDefault() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        checkUINotRecording(videoCaptureView);
    }

    @UiThreadTest
    @Test
    public void uiAfterUpdateNotRecording() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        videoCaptureView.updateUINotRecording();
        checkUINotRecording(videoCaptureView);
    }

    @UiThreadTest
    @Test
    public void uiAfterUpdateOngoing() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        videoCaptureView.updateUIRecordingOngoing();
        assertTrue(videoCaptureView.findViewById(R.id.videocapture_recordbtn_iv).isSelected());
        checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.VISIBLE);
        checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.GONE);
        checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.GONE);
        checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.GONE);
        checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.VISIBLE);
    }

    @UiThreadTest
    @Test
    public void uiAfterUpdateFinishedBitmapNull() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        videoCaptureView.updateUIRecordingFinished(null);
        checkVisibility(videoCaptureView, R.id.videocapture_recordbtn_iv, View.INVISIBLE);
        checkVisibility(videoCaptureView, R.id.videocapture_acceptbtn_iv, View.VISIBLE);
        checkVisibility(videoCaptureView, R.id.videocapture_declinebtn_iv, View.VISIBLE);
        checkVisibility(videoCaptureView, R.id.videocapture_preview_iv, View.VISIBLE);
        checkVisibility(videoCaptureView, R.id.videocapture_preview_sv, View.GONE);
    }

    @UiThreadTest
    @Test
    public void uiAfterUpdateFinishedBitmapNotNull() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
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

    @UiThreadTest
    @Test
    public void surfaceViewShouldNotBeNull() {
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
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
        final VideoCaptureView videoCaptureView = new VideoCaptureView(InstrumentationRegistry.getTargetContext());
        videoCaptureView.setRecordingButtonInterface(mockBtnInterface);
        final View btn = videoCaptureView.findViewById(btnResourceId);
        btn.performClick();
    }

}
