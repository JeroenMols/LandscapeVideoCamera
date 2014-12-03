package com.jmolsmobile.landscapevideocapture;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * @author Jeroen Mols
 */
public class CLog {

	public static final String	EXCEPTION		= "VideoCapture_Exception";
	public static final String	ACTIVITY		= "VideoCapture_Activity";
	public static final String	PREVIEW			= "VideoCapture_Preview";
	public static final String	HELPER			= "VideoCapture_CaptureHelper";
	public static final String	RECORDER		= "VideoCapture_VideoRecorder";

	private static boolean		mLoggingEnabled	= true;							;

	public static void toggleLogging(Context ctx) {
		mLoggingEnabled = (0 != (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
	}

	public static void d(String tag, String msg) {
		if (!mLoggingEnabled) return;
		Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (!mLoggingEnabled) return;
		Log.e(tag, msg);
	}

}
