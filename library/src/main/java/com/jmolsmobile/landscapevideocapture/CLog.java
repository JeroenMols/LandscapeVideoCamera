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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class CLog {

	public static final String	EXCEPTION		= "VideoCapture_Exception";
	public static final String	ACTIVITY		= "VideoCapture_Activity";
	public static final String	PREVIEW			= "VideoCapture_Preview";
	public static final String	HELPER			= "VideoCapture_CaptureHelper";
	public static final String	RECORDER		= "VideoCapture_VideoRecorder";
	public static final String	CAMERA		    = "VideoCapture_CameraWrapper";

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
