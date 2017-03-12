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

package com.jmolsmobile.landscapevideocapture.configuration;

import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;

import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureResolution;

public class CaptureConfiguration implements Parcelable {

    public static final int MBYTE_TO_BYTE = 1024 * 1024;
    public static final int MSEC_TO_SEC = 1000;

    public static final int NO_DURATION_LIMIT = -1;
    public static final int NO_FILESIZE_LIMIT = -1;

    private int videoWidth = PredefinedCaptureConfigurations.WIDTH_720P;
    private int videoHeight = PredefinedCaptureConfigurations.HEIGHT_720P;
    private int bitrate = PredefinedCaptureConfigurations.BITRATE_HQ_720P;
    private int maxDurationMs = NO_DURATION_LIMIT;
    private int maxFilesizeBytes = NO_FILESIZE_LIMIT;
    private boolean showTimer = false;
    private boolean allowFrontFacingCamera = true;
    private int videoFramerate = PredefinedCaptureConfigurations.FPS_30;     //Default FPS is 30.

    private int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private int AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC;
    private int VIDEO_SOURCE = MediaRecorder.VideoSource.CAMERA;
    private int VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264;

    public static CaptureConfiguration getDefault() {
        return new CaptureConfiguration();
    }

    private CaptureConfiguration() {
        // Default configuration
    }

    @Deprecated
    public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality) {
        videoWidth = resolution.width;
        videoHeight = resolution.height;
        bitrate = resolution.getBitrate(quality);
    }

    @Deprecated
    public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs,
                                int maxFilesizeMb, boolean showTimer) {
        this(resolution, quality, maxDurationSecs, maxFilesizeMb, showTimer, false);
        this.showTimer = showTimer;
    }

    @Deprecated
    public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs,
                                int maxFilesizeMb, boolean showTimer, boolean allowFrontFacingCamera) {
        this(resolution, quality, maxDurationSecs, maxFilesizeMb);
        this.showTimer = showTimer;
        this.allowFrontFacingCamera = allowFrontFacingCamera;
    }

    @Deprecated
    public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs,
                                int maxFilesizeMb, boolean showTimer, boolean allowFrontFacingCamera,
                                int videoFPS) {
        this(resolution, quality, maxDurationSecs, maxFilesizeMb, showTimer, allowFrontFacingCamera);
        videoFramerate = videoFPS;
    }

    @Deprecated
    public CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs,
                                int maxFilesizeMb) {
        this(resolution, quality);
        maxDurationMs = maxDurationSecs * MSEC_TO_SEC;
        maxFilesizeBytes = maxFilesizeMb * MBYTE_TO_BYTE;
    }

    @Deprecated
    public CaptureConfiguration(int videoWidth, int videoHeight, int bitrate) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.bitrate = bitrate;
    }

    @Deprecated
    public CaptureConfiguration(int videoWidth, int videoHeight, int bitrate, int maxDurationSecs, int maxFilesizeMb) {
        this(videoWidth, videoHeight, bitrate);
        maxDurationMs = maxDurationSecs * MSEC_TO_SEC;
        maxFilesizeBytes = maxFilesizeMb * MBYTE_TO_BYTE;
    }

    /**
     * @return Width of the captured video in pixels
     */
    public int getVideoWidth() {
        return videoWidth;
    }

    /**
     * @return Height of the captured video in pixels
     */
    public int getVideoHeight() {
        return videoHeight;
    }

    /**
     * @return Bitrate of the captured video in bits per second
     */
    public int getVideoBitrate() {
        return bitrate;
    }

    /**
     * @return Maximum duration of the captured video in milliseconds
     */
    public int getMaxCaptureDuration() {
        return maxDurationMs;
    }

    /**
     * @return Maximum filesize of the captured video in bytes
     */
    public int getMaxCaptureFileSize() {
        return maxFilesizeBytes;
    }

    /**
     * @return If timer must be displayed during video capture
     */
    public boolean getShowTimer() {
        return showTimer;
    }

    /**
     * @return If front facing camera toggle must be displayed before capturing video
     */
    public boolean getAllowFrontFacingCamera() {
        return allowFrontFacingCamera;
    }

    public int getOutputFormat() {
        return OUTPUT_FORMAT;
    }

    public int getAudioSource() {
        return AUDIO_SOURCE;
    }

    public int getAudioEncoder() {
        return AUDIO_ENCODER;
    }

    public int getVideoSource() {
        return VIDEO_SOURCE;
    }

    public int getVideoEncoder() {
        return VIDEO_ENCODER;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videoWidth);
        dest.writeInt(videoHeight);
        dest.writeInt(bitrate);
        dest.writeInt(maxDurationMs);
        dest.writeInt(maxFilesizeBytes);
        dest.writeInt(videoFramerate);
        dest.writeByte((byte) (showTimer ? 1 : 0));
        dest.writeByte((byte) (allowFrontFacingCamera ? 1 : 0));

        dest.writeInt(OUTPUT_FORMAT);
        dest.writeInt(AUDIO_SOURCE);
        dest.writeInt(AUDIO_ENCODER);
        dest.writeInt(VIDEO_SOURCE);
        dest.writeInt(VIDEO_ENCODER);
    }

    public static final Parcelable.Creator<CaptureConfiguration> CREATOR = new Parcelable.Creator<CaptureConfiguration>() {
        @Override
        public CaptureConfiguration createFromParcel(
                Parcel in) {
            return new CaptureConfiguration(in);
        }

        @Override
        public CaptureConfiguration[] newArray(
                int size) {
            return new CaptureConfiguration[size];
        }
    };

    private CaptureConfiguration(Parcel in) {
        videoWidth = in.readInt();
        videoHeight = in.readInt();
        bitrate = in.readInt();
        maxDurationMs = in.readInt();
        maxFilesizeBytes = in.readInt();
        videoFramerate = in.readInt();
        showTimer = in.readByte() != 0;
        allowFrontFacingCamera = in.readByte() != 0;

        OUTPUT_FORMAT = in.readInt();
        AUDIO_SOURCE = in.readInt();
        AUDIO_ENCODER = in.readInt();
        VIDEO_SOURCE = in.readInt();
        VIDEO_ENCODER = in.readInt();
    }

    public int getVideoFPS() {
        return videoFramerate;
    }

    public static class Builder {

        private final CaptureConfiguration configuration;

        public Builder(CaptureResolution resolution, CaptureQuality quality) {
            configuration = new CaptureConfiguration();
            configuration.videoWidth = resolution.width;
            configuration.videoHeight = resolution.height;
            configuration.bitrate = resolution.getBitrate(quality);
        }

        public Builder(int width, int height, int bitrate) {
            configuration = new CaptureConfiguration();
            configuration.videoWidth = width;
            configuration.videoHeight = height;
            configuration.bitrate = bitrate;
        }

        public CaptureConfiguration build() {
            return configuration;
        }

        public Builder maxDuration(int maxDurationSec) {
            configuration.maxDurationMs = maxDurationSec * MSEC_TO_SEC;
            return this;
        }

        public Builder maxFileSize(int maxFileSizeMb) {
            configuration.maxFilesizeBytes = maxFileSizeMb * MBYTE_TO_BYTE;
            return this;
        }

        public Builder frameRate(int framesPerSec) {
            configuration.videoFramerate = framesPerSec;
            return this;
        }

        public Builder showRecordingTime() {
            configuration.showTimer = true;
            return this;
        }

        public Builder noCameraToggle() {
            configuration.allowFrontFacingCamera = false;
            return this;
        }
    }
}