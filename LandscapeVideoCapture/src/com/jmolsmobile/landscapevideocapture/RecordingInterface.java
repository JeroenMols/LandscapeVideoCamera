package com.jmolsmobile.landscapevideocapture;

public interface RecordingInterface {

	// METHODS TO CONTROL THE RECORDING
	public abstract boolean startRecording();

	public abstract boolean stopRecording();

	public abstract void finishCancelled();

	public abstract boolean isRecording();

	public abstract void setRecording(boolean mRecording);

	public abstract void finishCompleted();

}