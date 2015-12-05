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
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.SurfaceHolder;

import com.jmolsmobile.landscapevideocapture.CLog;
import com.jmolsmobile.landscapevideocapture.camera.OpenCameraException.OpenType;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraWrapper {

    private final int mDisplayRotation;
    private Camera     mCamera     = null;
    private Parameters mParameters = null;

    public CameraWrapper(int displayRotation) {
        mDisplayRotation = displayRotation;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void openCamera() throws OpenCameraException {
        mCamera = null;
        try {
            mCamera = openCameraFromSystem();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new OpenCameraException(OpenType.INUSE);
        }

        if (mCamera == null) throw new OpenCameraException(OpenType.NOCAMERA);
    }

    public void prepareCameraForRecording() throws PrepareCameraException {
        try {
            storeCameraParametersBeforeUnlocking();
            unlockCameraFromSystem();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new PrepareCameraException();
        }
    }

    public void releaseCamera() {
        if (getCamera() == null) return;
        releaseCameraFromSystem();
    }

    public void startPreview(final SurfaceHolder holder) throws IOException {
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
    }

    public void stopPreview() throws Exception {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
    }

    public RecordingSize getSupportedRecordingSize(int width, int height) {
        Camera.Size recordingSize = getOptimalSize(getSupportedVideoSizes(VERSION.SDK_INT), width, height);
        if (recordingSize == null) {
            CLog.e(CLog.CAMERA, "Failed to find supported recording size - falling back to requested: " + width + "x" + height);
            return new RecordingSize(width, height);
        }
        CLog.d(CLog.CAMERA, "Recording size: " + recordingSize.width + "x" + recordingSize.height);
        return new RecordingSize(recordingSize.width, recordingSize.height);
    }

    public CamcorderProfile getBaseRecordingProfile() {
        CamcorderProfile returnProfile;
        if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        } else {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
        return returnProfile;
    }

    public void configureForPreview(int viewWidth, int viewHeight) {
        final Parameters params = getCameraParametersFromSystem();
        final Size previewSize = getOptimalSize(params.getSupportedPreviewSizes(), viewWidth, viewHeight);
        params.setPreviewSize(previewSize.width, previewSize.height);
        params.setPreviewFormat(ImageFormat.NV21);
        updateCameraParametersFromSystem(params);
        CLog.d(CLog.CAMERA, "Preview size: " + previewSize.width + "x" + previewSize.height);
    }

    public void enableAutoFocus() {
        final Parameters params = getCameraParametersFromSystem();
        params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        updateCameraParametersFromSystem(params);
    }

    public int getDisplayOrientation() {
        return mDisplayRotation * 90;
    }

    protected Camera openCameraFromSystem() {
        return Camera.open(CameraInfo.CAMERA_FACING_BACK);
    }

    protected void unlockCameraFromSystem() {
        mCamera.unlock();
    }

    protected void releaseCameraFromSystem() {
        mCamera.release();
    }

    protected Parameters getCameraParametersFromSystem() {
        return mCamera.getParameters();
    }

    protected void updateCameraParametersFromSystem(Parameters params) {
        mCamera.setParameters(params);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    protected List<Size> getSupportedVideoSizes(int currentSdkInt) {
        Parameters params = getCameraParametersAfterUnlocking();

        List<Size> supportedVideoSizes;
        if (currentSdkInt < Build.VERSION_CODES.HONEYCOMB) {
            CLog.e(CLog.CAMERA, "Using supportedPreviewSizes iso supportedVideoSizes due to API restriction");
            supportedVideoSizes = params.getSupportedPreviewSizes();
        } else if (params.getSupportedVideoSizes() == null) {
            CLog.e(CLog.CAMERA, "Using supportedPreviewSizes because supportedVideoSizes is null");
            supportedVideoSizes = params.getSupportedPreviewSizes();
        } else {
            supportedVideoSizes = params.getSupportedVideoSizes();
        }

        return supportedVideoSizes;
    }

    protected void storeCameraParametersBeforeUnlocking() {
        mParameters = getCameraParametersFromSystem();
    }

    private Parameters getCameraParametersAfterUnlocking() {
        return mParameters;
    }

    /**
     * Copyright (C) 2013 The Android Open Source Project
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
    public Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        final double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        final int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (final Camera.Size size : sizes) {
            final double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (final Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
