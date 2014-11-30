package com.jmolsmobile.landscapevideocapture;

/**
 * @author Jeroen Mols
 */
public class CameraException extends Exception {

	private static final String	LOG_PREFIX			= "Unable to open/prepare camera - ";
	private static final long	serialVersionUID	= -7340415176385044242L;

	enum OpenType {
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

	public CameraException(OpenType type) {
		super(type.getMessage());
		mType = type;
	}

	@Override
	public void printStackTrace() {
		CLog.e(CLog.EXCEPTION, LOG_PREFIX + mType.getMessage());
		super.printStackTrace();
	}
}
