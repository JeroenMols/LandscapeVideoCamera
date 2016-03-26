LandscapeVideoCamera [![Build Status](https://travis-ci.org/JeroenMols/LandscapeVideoCamera.svg?branch=master)](https://travis-ci.org/JeroenMols/LandscapeVideoCamera) [![codecov.io](https://codecov.io/github/JeroenMols/LandscapeVideoCamera/coverage.svg?branch=master)](https://codecov.io/github/JeroenMols/LandscapeVideoCamera?branch=master)  [![Release](https://jitpack.io/v/jeroenmols/landscapevideocamera.svg)](https://jitpack.io/#jeroenmols/LandscapeVideoCamera)
=====================

Highly flexible Android Camera which offers granular control over the video quality and filesize, while restricting recordings to be landscape only.

<a href="https://play.google.com/store/apps/details?id=com.jmolsmobile.landscapevideocapture_sample&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1">
  <img alt="Get it on Google Play" width="200"
        src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" />
</a>

There are a number of issues with the default Android intent to capture videos (`MediaStore.ACTION_VIDEO_CAPTURE`) which led me to create this library project:

1. The default intent only accepts integer quality parameters of 0 (MMS quality) or 1 (highest available quality), using the intent extra `MediaStore.EXTRA_VIDEO_QUALITY`.
2. The default intent does not return the URI of the recorded file if it was specified when launching the intent.
3. The default intent doesn't care whether users capture their video in portrait mode or landscape.

<p align="center">
  <img src="https://raw.githubusercontent.com/JeroenMols/LandscapeVideoCamera/master/screenshots/preview.gif" alt="LandscapeVideoCamera in action" height="550"/>
</p>

## Features

This library provides a full and reusable custom camera, which:

* Forces the users to rotate their device to landscape
* Allows to specify the filename, or have the library generate one for you
* Allows very granular control over the capture settings:
  * Resolution
  * Bitrate
  * Max filesize
  * Max video duration
  * audio/video codec
  * ...

## Screenshots

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_2.png" alt="Portrait" height="450"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_3.png" alt="Not recording" width="450"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_4.png" alt="Not recording" width="450"/>
</p>

<p align="center">
  <img src="https://raw.github.com/jmolsmobile/LandscapeVideoCapture/master/playstore/screenshot_5.png" alt="Not recording" width="450"/>
</p>



## How to use

  1. Add the Jitpack repository to your project:
```groovy
          repositories {
              maven { url "https://jitpack.io" }
          }
```
  2. Add a dependency on the library:
```groovy
          compile 'com.github.JeroenMols:LandscapeVideoCamera:1.1.3'
```
  3. Specify the VideoCaptureActivity in your manifest:
```xml
         <activity
             android:name="com.jmolsmobile.landscapevideocapture.VideoCaptureActivity"
             android:screenOrientation="sensor" >
         </activity>
```
  4. Request the following permissions in your manifest:
```xml
         <uses-permission android:name="android.permission.RECORD_AUDIO" />
         <uses-permission android:name="android.permission.CAMERA" />
         <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
  5. Create a CaptureConfiguration - object with the desired parameters. (optional)
```java
         CaptureConfiguration configuration = CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality);
         CaptureConfiguration configuration = CaptureConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs, int maxFilesizeMb);
         CaptureConfiguration configuration = CaptureConfiguration(int videoWidth, int videoHeight, int bitrate);
         CaptureConfiguration configuration = CaptureConfiguration(int videoWidth, int videoHeight, int bitrate, int maxDurationSecs, int maxFilesizeMb);
```
  Note: When no CaptureConfiguration is specified, a default configuration will be used.

  Note 2: Subclass the CaptureConfiguration class to set more advanced configurations. (codecs, audio bitrate,...)

  6. Launch the `VideoCaptureActivity` for result, add the CaptureConfiguration as an parcelable extra `EXTRA_CAPTURE_CONFIGURATION` and optionally add a String extra `EXTRA_OUTPUT_FILENAME`.
```java
         final Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
         intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
         intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
         startActivityForResult(intent, RESULT_CODE);
```
  7. Check the resultcode (`RESULT_OK`, `RESULT_CANCELLED` or `VideoCaptureActivity.RESULT_ERROR`) and in case of success get the output filename in the intent extra `EXTRA_OUTPUT_FILENAME`.

## Questions
@molsjeroen


## Thanks
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-LandscapeVideoCamera-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1209)
