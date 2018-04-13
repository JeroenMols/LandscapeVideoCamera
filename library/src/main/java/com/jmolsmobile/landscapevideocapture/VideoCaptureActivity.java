/*
 *  Copyright 2016 Jeroen Mols
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture;

import com.jmolsmobile.landscapevideocapture.camera.CameraWrapper;
import com.jmolsmobile.landscapevideocapture.camera.NativeCamera;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.recorder.AlreadyUsedException;
import com.jmolsmobile.landscapevideocapture.recorder.VideoRecorder;
import com.jmolsmobile.landscapevideocapture.recorder.VideoRecorderInterface;
import com.jmolsmobile.landscapevideocapture.view.RecordingButtonInterface;
import com.jmolsmobile.landscapevideocapture.view.VideoCaptureView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class VideoCaptureActivity extends Activity implements RecordingButtonInterface, VideoRecorderInterface {

    public static final int RESULT_ERROR = 753245;

    private static final int REQUESTCODE_SWITCHCAMERA = 578465;

    public static final String EXTRA_OUTPUT_FILENAME = "com.jmolsmobile.extraoutputfilename";

    public static final String EXTRA_CAPTURE_CONFIGURATION = "com.jmolsmobile.extracaptureconfiguration";

    public static final String EXTRA_ERROR_MESSAGE = "com.jmolsmobile.extraerrormessage";

    private static final String EXTRA_FRONTFACINGCAMERASELECTED = "com.jmolsmobile.extracamerafacing";

    private static final String SAVED_RECORDED_BOOLEAN = "com.jmolsmobile.savedrecordedboolean";

    protected static final String SAVED_OUTPUT_FILENAME = "com.jmolsmobile.savedoutputfilename";

    private boolean mVideoRecorded = false;

    VideoFile mVideoFile = null;

    private CaptureConfiguration mCaptureConfiguration;

    private VideoCaptureView mVideoCaptureView;

    private VideoRecorder mVideoRecorder;

    private boolean isFrontFacingCameraSelected;

    private CameraWrapper mCameraWrapper;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.toggleLogging(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videocapture);

        initializeCaptureConfiguration(savedInstanceState);

        mVideoCaptureView = (VideoCaptureView) findViewById(R.id.videocapture_videocaptureview_vcv);
        if (mVideoCaptureView == null) {
            return; // Wrong orientation
        }

        initializeRecordingUI();
    }

    private void initializeCaptureConfiguration(final Bundle savedInstanceState) {
        mCaptureConfiguration = generateCaptureConfiguration();
        mVideoRecorded = generateVideoRecorded(savedInstanceState);
        mVideoFile = generateOutputFile(savedInstanceState);
        isFrontFacingCameraSelected = generateIsFrontFacingCameraSelected();
    }

    private void initializeRecordingUI() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        mCameraWrapper = new CameraWrapper(new NativeCamera(), display.getRotation());

        mVideoRecorder = new VideoRecorder(this,
                mCaptureConfiguration,
                mVideoFile,
                mCameraWrapper,
                mVideoCaptureView.getPreviewSurfaceHolder(),
                isFrontFacingCameraSelected);

        mVideoCaptureView.setRecordingButtonInterface(this);
        mVideoCaptureView.setCameraSwitchingEnabled(mCaptureConfiguration.getAllowFrontFacingCamera());
        mVideoCaptureView.setCameraFacing(isFrontFacingCameraSelected);
        mVideoCaptureView.setFlashSwitchingEnabled(mCaptureConfiguration.getAllowFlashToggle(), isFrontFacingCameraSelected);
        mVideoCaptureView.setFlashStartOption(mCameraWrapper, mCaptureConfiguration.getIfFlashStartOn());

        if (mVideoRecorded) {
            mVideoCaptureView.updateUIRecordingFinished(getVideoThumbnail());
        } else {
            mVideoCaptureView.updateUINotRecording();
        }
        mVideoCaptureView.showTimer(mCaptureConfiguration.getShowTimer());
    }

    @Override
    protected void onPause() {
        if (mVideoRecorder != null) {
            mVideoRecorder.stopRecording(null);
        }
        releaseAllResources();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finishCancelled();
    }

    @Override
    public void onRecordButtonClicked() {
        try {
            mVideoRecorder.toggleRecording();
        } catch (AlreadyUsedException e) {
            CLog.d(CLog.ACTIVITY, "Cannot toggle recording after cleaning up all resources");
        }
    }

    @Override
    public void onAcceptButtonClicked() {
        finishCompleted();
    }

    @Override
    public void onDeclineButtonClicked() {
        finishCancelled();
    }

    @Override
    public void onRecordingStarted() {
        mVideoCaptureView.updateUIRecordingOngoing();
    }

    @Override
    public void onSwitchCamera(boolean isFrontFacingSelected) {
        Intent intent = new Intent(VideoCaptureActivity.this, VideoCaptureActivity.class);
        intent.putExtras(getIntent().getExtras());      //Pass all the current intent parameters
        intent.putExtra(EXTRA_FRONTFACINGCAMERASELECTED, isFrontFacingSelected);
        startActivityForResult(intent, REQUESTCODE_SWITCHCAMERA);
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

    @Override
    public void onFlashButtonClicked(boolean isFlashOn) {
        mCameraWrapper.setFlash(isFlashOn);
    }

    @Override
    public void onRecordingStopped(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        mVideoCaptureView.updateUIRecordingFinished(getVideoThumbnail());
        releaseAllResources();
    }

    @Override
    public void onRecordingSuccess() {
        mVideoRecorded = true;
    }

    @Override
    public void onRecordingFailed(String message) {
        finishError(message);
    }

    private void finishCompleted() {
        final Intent result = new Intent();
        result.putExtra(EXTRA_OUTPUT_FILENAME, mVideoFile.getFullPath());
        this.setResult(RESULT_OK, result);
        finish();
    }

    private void finishCancelled() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    private void finishError(final String message) {
        Toast.makeText(getApplicationContext(), "Can't capture video: " + message, Toast.LENGTH_LONG).show();

        final Intent result = new Intent();
        result.putExtra(EXTRA_ERROR_MESSAGE, message);
        this.setResult(RESULT_ERROR, result);
        finish();
    }

    private void releaseAllResources() {
        if (mVideoRecorder != null) {
            mVideoRecorder.releaseAllResources();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SAVED_RECORDED_BOOLEAN, mVideoRecorded);
        savedInstanceState.putString(SAVED_OUTPUT_FILENAME, mVideoFile.getFullPath());
        super.onSaveInstanceState(savedInstanceState);
    }

    protected CaptureConfiguration generateCaptureConfiguration() {
        CaptureConfiguration returnConfiguration = this.getIntent().getParcelableExtra(EXTRA_CAPTURE_CONFIGURATION);
        if (returnConfiguration == null) {
            returnConfiguration = CaptureConfiguration.getDefault();
            CLog.d(CLog.ACTIVITY, "No captureconfiguration passed - using default configuration");
        }
        return returnConfiguration;
    }

    private boolean generateVideoRecorded(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }
        return savedInstanceState.getBoolean(SAVED_RECORDED_BOOLEAN, false);
    }

    protected VideoFile generateOutputFile(Bundle savedInstanceState) {
        VideoFile returnFile;
        if (savedInstanceState != null) {
            returnFile = new VideoFile(savedInstanceState.getString(SAVED_OUTPUT_FILENAME));
        } else {
            returnFile = new VideoFile(this.getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME));
        }
        // TODO: add checks to see if outputfile is writeable
        return returnFile;
    }

    private boolean generateIsFrontFacingCameraSelected() {
        return getIntent().getBooleanExtra(EXTRA_FRONTFACINGCAMERASELECTED, false);
    }

    public Bitmap getVideoThumbnail() {
        final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoFile.getFullPath(),
                Thumbnails.FULL_SCREEN_KIND);
        if (thumbnail == null) {
            CLog.d(CLog.ACTIVITY, "Failed to generate video preview");
        }
        return thumbnail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.setResult(resultCode, data);
        finish();
    }
}
