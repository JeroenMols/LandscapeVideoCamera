package com.jmolsmobile.landscapevideocapture;

import android.util.Log;

/**
 * @author Jeroen Mols
 */
public class CLog {

	public static final String	EXCEPTION	= "VideoCapture_Exception";
	public static final String	ACTIVITY	= "VideoCapture_Activity";
	public static final String	PREVIEW		= "VideoCapture_Preview";
	public static final String	HELPER		= "VideoCapture_CaptureHelper";
	public static final String	RECORDER	= "VideoCapture_VideoRecorder";

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}

}
