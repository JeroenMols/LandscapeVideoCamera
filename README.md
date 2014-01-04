LandscapeVideoCapture
=====================

Android MediaRecoder that forces users to use their device in landscape mode + has adjustable quality parameters

There are a number of issues with the default Android intent to capture videos (MediaStore.ACTION_VIDEO_CAPTURE) which led me to create this library project:

1. The default intent only accepts integer quality parameters of 0 (MMS quality) or 1 (highest available quality), using the intent extra MediaStore.EXTRA_VIDEO_QUALITY.
2. The default intent does not return the URI of the recorded file if it was specified when launching the intent.
3. The default intent doesn't care wheter users capture their video in portait mode or landscape.

Therefore this library provides a full and reusable code sample of a custom camera recording intent, which:

* Forces the users to rotate their device to landscape
* Allows developers to pass an output file URI as an intent extra String (EXTRA_OUTPUT_FILENAME), which is also returned on a successful capture.
* Allows developers to specify capture quality parameters in code: resolution, framerate, bitrate, audio/video codec,...

Note that this is still a work in progress and it still very much needs a code clean-up. :)
