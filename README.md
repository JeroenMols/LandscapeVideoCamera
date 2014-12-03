LandscapeVideoCapture
=====================

Android MediaRecoder that forces users to use their device in landscape mode + has adjustable quality parameters

There are a number of issues with the default Android intent to capture videos (`MediaStore.ACTION_VIDEO_CAPTURE`) which led me to create this library project:

1. The default intent only accepts integer quality parameters of 0 (MMS quality) or 1 (highest available quality), using the intent extra `MediaStore.EXTRA_VIDEO_QUALITY`.
2. The default intent does not return the URI of the recorded file if it was specified when launching the intent.
3. The default intent doesn't care wheter users capture their video in portait mode or landscape.

<a href="https://play.google.com/store/apps/details?id=com.jmolsmobile.landscapevideocapture_sample">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

## Features

This library provides a full and reusable code sample of a custom camera recording intent, which:

* Forces the users to rotate their device to landscape
* Allows developers to pass an output file URI as an intent extra String (`EXTRA_OUTPUT_FILENAME`), which is also returned on a successful capture. (If not output file is specified, the class will generate one)
* Allows developers to specify capture quality parameters in code: resolution, framerate, bitrate, audio/video codec,...

## Screenshots

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_2.png" alt="Portrait" height="600"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_3.png" alt="Not recording" width="600"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_4.png" alt="Not recording" width="600"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_5.png" alt="Not recording" width="600"/>
</p>



## How to use

  1. Include "LandscapeVideoCamera" as a library project in your own project
  2. Specify the VideoCaptureActivity in your manifest:
  
         <activity
             android:name="com.jmolsmobile.landscapevideocapture.VideoCaptureActivity"
             android:screenOrientation="sensor" >
         </activity>

  3. Request the following permissions in your manifest: 

         <uses-permission android:name="android.permission.RECORD_AUDIO" />
         <uses-permission android:name="android.permission.CAMERA" />
         <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  
  4. Create a CaptureConfiguration - object with the desired parameters. (optional)

         Capture configuration = CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality);
         Capture configuration = CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs, int maxFilesizeMb);
         Capture configuration = CaptureConfiguration(int videoWidth, int videoHeight, int bitrate);
         Capture configuration = CaptureConfiguration(int videoWidth, int videoHeight, int bitrate, int maxDurationSecs, int maxFilesizeMb);
  
  Note: When no CaptureConfiguration is specified, a default configuration will be used.
  
  Note 2: Subclass the CaptureConfiguration class to set more advanced configurations. (codecs, audio bitrate,...)
  
  5. Launch the `VideoCaptureActivity` for result, add the CaptureConfiguration as an parcelable extra `EXTRA_CAPTURE_CONFIGURATION` and optionally add a String extra `EXTRA_OUTPUT_FILENAME`.

         final Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
         intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
         intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
         startActivityForResult(intent, RESULT_CODE);

  6. Check the resultcode (`RESULT_OK`, `RESULT_CANCELLED` or `VideoCaptureActivity.RESULT_ERROR`) and in case of success get the output filename in the intent extra `EXTRA_OUTPUT_FILENAME`.
