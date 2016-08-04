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

package com.jmolsmobile.landscapevideocapture.camera;

import com.jmolsmobile.landscapevideocapture.CLog;

public class OpenCameraException extends Exception {

	private static final String	LOG_PREFIX			= "Unable to open camera - ";
	private static final long	serialVersionUID	= -7340415176385044242L;

	public enum OpenType {
		INUSE("Camera disabled or in use by other process"), NOCAMERA("Device does not have camera");

		private String	mMessage;

		private OpenType(String msg) {
			mMessage = msg;
		}

		public String getMessage() {
			return mMessage;
		}

	}

	private final OpenType	mType;

	public OpenCameraException(OpenType type) {
		super(type.getMessage());
		mType = type;
	}

	@Override
	public void printStackTrace() {
		CLog.e(CLog.EXCEPTION, LOG_PREFIX + mType.getMessage());
		super.printStackTrace();
	}
}
