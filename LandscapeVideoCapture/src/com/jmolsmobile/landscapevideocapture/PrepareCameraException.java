package com.jmolsmobile.landscapevideocapture;

/**
 * @author Jeroen Mols
 */
public class PrepareCameraException extends Exception {

	private static final String	MESSAGE	= "Unable to use camera for recording";

	@Override
	public String getMessage() {
		return MESSAGE;
	}
}
