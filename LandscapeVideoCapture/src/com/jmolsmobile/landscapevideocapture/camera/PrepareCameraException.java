package com.jmolsmobile.landscapevideocapture.camera;

import com.jmolsmobile.landscapevideocapture.CLog;

/**
 * @author Jeroen Mols
 */
public class PrepareCameraException extends Exception {

	private static final String	LOG_PREFIX			= "Unable to unlock camera - ";
	private static final String	MESSAGE				= "Unable to use camera for recording";

	private static final long	serialVersionUID	= 6305923762266448674L;

	@Override
	public String getMessage() {
		CLog.e(CLog.EXCEPTION, LOG_PREFIX + MESSAGE);
		return MESSAGE;
	}
}
