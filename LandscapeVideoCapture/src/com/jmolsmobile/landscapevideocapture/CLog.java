package com.jmolsmobile.landscapevideocapture;

import android.util.Log;

/**
 * @author Jeroen Mols
 */
public class CLog {

	public static final String	EXCEPTION	= "VideoCapture_Exception";
	public static final String	ACTIVITY	= "VideoCapture_Activity";

	public static void v(String tag, String msg) {
		Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}

}
